package com.bumble.appyx.transitionmodel.backstack.interpolator

//import com.bumble.appyx.interactions.Logger
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.ui.BaseProps
import com.bumble.appyx.interactions.core.ui.MatchedProps
import com.bumble.appyx.interactions.core.ui.TransitionBounds
import com.bumble.appyx.interactions.core.ui.property.Animatable
import com.bumble.appyx.interactions.core.ui.property.HasModifier
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.property.impl.Offset
import com.bumble.appyx.transitionmodel.BaseInterpolator
import com.bumble.appyx.transitionmodel.backstack.BackStackModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BackStackSlider<NavTarget : Any>(
    private val transitionBounds: TransitionBounds,
    coroutineScope: CoroutineScope
) : BaseInterpolator<NavTarget, BackStackModel.State<NavTarget>, BackStackSlider.Props>(
    coroutineScope = coroutineScope
) {
    private val width = transitionBounds.widthDp

    override fun defaultProps(): Props =
        Props(screenWidth = transitionBounds.widthDp)

    data class Props(
        val offset: Offset = Offset(DpOffset(0.dp, 0.dp)),
        val alpha: Alpha = Alpha(value = 1f),
        val offsetMultiplier: Int = 1,
        val screenWidth: Dp
    ) : HasModifier, BaseProps(), Animatable<Props> {

        override fun calculateVisibilityState() {
            _visibilityState.update {
                alpha.value > 0.0f && offset.value.x < screenWidth && offset.value.x > -screenWidth
            }
        }

        override val modifier: Modifier
            get() = Modifier
                .then(offset.modifier)
                .then(alpha.modifier)

        override suspend fun animateTo(
            scope: CoroutineScope,
            props: Props,
            springSpec: SpringSpec<Float>,
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
                offset.animateTo(
                    props.offset.value,
                    spring(animationSpec.dampingRatio, animationSpec.stiffness)
                ) { calculateVisibilityState() }
            }
            val a2 = scope.async {
                alpha.animateTo(
                    props.alpha.value,
                    spring(animationSpec.dampingRatio, animationSpec.stiffness)
                ) { calculateVisibilityState() }
            }
            awaitAll(a1, a2)
            onFinished()
        }

        override suspend fun snapTo(scope: CoroutineScope, props: Props) {
            scope.launch {
                val a1 = async { offset.snapTo(props.offset.value) }
                val a2 = async { alpha.snapTo(props.alpha.value) }
                awaitAll(a1, a2)
                calculateVisibilityState()
            }
        }

        override fun lerpTo(scope: CoroutineScope, start: Props, end: Props, fraction: Float) {
            scope.launch {
                offset.lerpTo(start.offset, end.offset, fraction)
                alpha.lerpTo(start.alpha, end.alpha, fraction)
                calculateVisibilityState()
            }
        }
    }

    private val outsideLeft = Props(
        offset = Offset(DpOffset(-width, 0.dp)),
        screenWidth = width
    )

    private val outsideRight = Props(
        offset = Offset(DpOffset(width, 0.dp)),
        screenWidth = width
    )

    private val noOffset = Props(
        offset = Offset(DpOffset(0.dp, 0.dp)),
        screenWidth = width
    )


    override fun BackStackModel.State<NavTarget>.toProps(): List<MatchedProps<NavTarget, Props>> =
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


}
