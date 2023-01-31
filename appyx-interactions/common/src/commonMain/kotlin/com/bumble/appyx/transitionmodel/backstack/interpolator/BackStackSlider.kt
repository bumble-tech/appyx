package com.bumble.appyx.transitionmodel.backstack.interpolator

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.Logger
import com.bumble.appyx.interactions.core.Segment
import com.bumble.appyx.interactions.core.Update
import com.bumble.appyx.interactions.core.ui.BaseProps
import com.bumble.appyx.interactions.core.ui.FrameModel
import com.bumble.appyx.interactions.core.ui.Interpolator
import com.bumble.appyx.interactions.core.ui.MatchedProps
import com.bumble.appyx.interactions.core.ui.TransitionBounds
import com.bumble.appyx.interactions.core.ui.property.HasModifier
import com.bumble.appyx.interactions.core.ui.property.Interpolatable
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.property.impl.Offset
import com.bumble.appyx.transitionmodel.backstack.BackStackModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking

class BackStackSlider<NavTarget>(
    transitionBounds: TransitionBounds
) : Interpolator<NavTarget, BackStackModel.State<NavTarget>> {
    private val width = transitionBounds.widthDp

    data class Props(
        val offset: Offset = Offset(DpOffset(0.dp, 0.dp)),
        val alpha: Alpha = Alpha(value = 1f),
        val offsetMultiplier: Int = 1,
        override val isVisible: Boolean = true
    ) : Interpolatable<Props>, HasModifier, BaseProps {

        override val modifier: Modifier
            get() = Modifier
                .then(offset.modifier)
                .then(alpha.modifier)

        override suspend fun lerpTo(start: Props, end: Props, fraction: Float) {
            offset.lerpTo(start.offset, end.offset, fraction)
            alpha.lerpTo(start.alpha, end.alpha, fraction)
        }

        suspend fun animateTo(
            scope: CoroutineScope,
            props: Props,
            onStart: () -> Unit,
            onFinished: () -> Unit
        ) {
            // FIXME this should match the own animationSpec of the model (which can also be supplied
            //  from operation extension methods) rather than created here
            val animationSpec: SpringSpec<Float> = spring(
                stiffness = Spring.StiffnessVeryLow / 5,
                dampingRatio = Spring.DampingRatioLowBouncy,
            )
            onStart()
            val a1 = scope.async {
                offset.animateTo(props.offset.value, spring(animationSpec.dampingRatio, animationSpec.stiffness))
            }
            val a2 = scope.async {
                alpha.animateTo(props.alpha.value, spring(animationSpec.dampingRatio, animationSpec.stiffness))
            }
            awaitAll(a1, a2)
            onFinished()
        }

    }

    private val outsideLeft = Props(
        offset = Offset(DpOffset(-width, 0.dp)),
        isVisible = false
    )

    private val outsideRight = Props(
        offset = Offset(DpOffset(width, 0.dp)),
        isVisible = false
    )

    private val noOffset = Props(
        offset = Offset(DpOffset(0.dp, 0.dp)),
        isVisible = true
    )

    private fun <NavTarget> BackStackModel.State<NavTarget>.toProps(): List<MatchedProps<NavTarget, Props>> =
        created.map { MatchedProps(it, outsideRight) } +
                listOf(MatchedProps(active, noOffset)) +
                stashed.mapIndexed { index, navElement ->
                    MatchedProps(
                        navElement,
                        outsideLeft.copy(offsetMultiplier = index + 1)
                    )
                } +
                destroyed.map { navElement ->
                    MatchedProps(
                        navElement,
                        outsideRight.copy(alpha = Alpha(0f))
                    )
                }

    override fun mapSegment(segment: Segment<BackStackModel.State<NavTarget>>, segmentProgress: Float): List<FrameModel<NavTarget>> {
        val (fromState, targetState) = segment.navTransition
        val fromProps = fromState.toProps()
        val targetProps = targetState.toProps()

        // TODO: use a map instead of find
        return targetProps.map { t1 ->
            val t0 = fromProps.find { it.element.id == t1.element.id }!!
            val elementProps = cache.getOrPut(t1.element.id) { Props() }

            runBlocking {
                elementProps.lerpTo(t0.props, t1.props, segmentProgress)
            }

            FrameModel(
                navElement = t1.element,
                modifier = elementProps.modifier
                    .composed { this },
                progress = segmentProgress,
                state = resolveNavElementVisibility(t0.props, t1.props)
            )
        }
    }

    private val cache: MutableMap<String, Props> = mutableMapOf()
    private val animations: MutableMap<String, Boolean> = mutableMapOf()
    private val isAnimating: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override fun isAnimating(): StateFlow<Boolean> = isAnimating

    fun updateAnimationState(key: String, isAnimating: Boolean) {
        animations[key] = isAnimating
        this.isAnimating.update { isAnimating || animations.any { it.value } }
    }

    operator fun DpOffset.times(multiplier: Int) =
        DpOffset(x * multiplier, y * multiplier)

    override fun mapUpdate(update: Update<BackStackModel.State<NavTarget>>): List<FrameModel<NavTarget>> {

        val targetProps = update.currentTargetState.toProps()

        return targetProps.map { t1 ->
            val elementProps = cache.getOrPut(t1.element.id) { Props() }

            FrameModel(
                navElement = t1.element,
                modifier = elementProps.modifier
                    .composed {
                        LaunchedEffect(t1.props) {
                            Logger.log("TestDrive", "Animating ${t1.element} to ${t1.props}")
                            elementProps.animateTo(
                                scope = this,
                                props = t1.props,
                                onStart = { updateAnimationState(t1.element.id, true) },
                                onFinished = { updateAnimationState(t1.element.id, false) },
                            )
                        }
                        this
                    },
                progress = 1f
            )
        }
    }

}
