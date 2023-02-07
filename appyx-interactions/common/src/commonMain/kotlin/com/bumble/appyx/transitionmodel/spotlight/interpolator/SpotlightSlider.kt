package com.bumble.appyx.transitionmodel.spotlight.interpolator

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.Keyframes
import com.bumble.appyx.interactions.core.Operation
import com.bumble.appyx.interactions.core.Operation.Mode.KEYFRAME
import com.bumble.appyx.interactions.core.TransitionModel
import com.bumble.appyx.interactions.core.Update
import com.bumble.appyx.interactions.core.inputsource.Gesture
import com.bumble.appyx.interactions.core.ui.BaseProps
import com.bumble.appyx.interactions.core.ui.FrameModel
import com.bumble.appyx.interactions.core.ui.GestureFactory
import com.bumble.appyx.interactions.core.ui.Interpolator
import com.bumble.appyx.interactions.core.ui.MatchedProps
import com.bumble.appyx.interactions.core.ui.TransitionBounds
import com.bumble.appyx.interactions.core.ui.geometry.Geometry1D
import com.bumble.appyx.interactions.core.ui.property.Animatable
import com.bumble.appyx.interactions.core.ui.property.HasModifier
import com.bumble.appyx.interactions.core.ui.property.Interpolatable
import com.bumble.appyx.transitionmodel.BaseInterpolator
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel.State.ElementState.CREATED
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel.State.ElementState.DESTROYED
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel.State.ElementState.STANDARD
import com.bumble.appyx.transitionmodel.spotlight.operation.Next
import com.bumble.appyx.transitionmodel.spotlight.operation.Previous
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

typealias OffsetP = com.bumble.appyx.interactions.core.ui.property.impl.Offset

class SpotlightSlider<NavTarget : Any>(
    transitionBounds: TransitionBounds,
    private val scope: CoroutineScope,
    private val orientation: Orientation = Orientation.Horizontal, // TODO support RTL
) : BaseInterpolator<NavTarget, SpotlightModel.State<NavTarget>, SpotlightSlider.Props>() {
    private val width = transitionBounds.widthDp
    private val height = transitionBounds.heightDp

    private val scroll = Geometry1D<TransitionModel.Output<SpotlightModel.State<NavTarget>>, List<FrameModel<NavTarget>>>(
        scope = scope,
        initialValue = 0f // TODO sync this with the model's initial value
    ) {
        mapCore(it)
    }

    data class Props(
        val offset: OffsetP,
        val scale: Float = 1f,
        val alpha: Float = 1f,
        val zIndex: Float = 1f,
        val aspectRatio: Float = 0.42f,
        val rotation: Float = 0f,
        // TODO dynamic visibility calculation
        override val isVisible: Boolean = true
    ) : Interpolatable<Props>, HasModifier, Animatable<Props>, BaseProps {


        override val modifier: Modifier
            get() = Modifier
                .then(offset.modifier)

        override suspend fun snapTo(scope: CoroutineScope, props: Props) {
            offset.snapTo(props.offset.value)
        }

        override suspend fun lerpTo(start: Props, end: Props, fraction: Float) {
            offset.lerpTo(start.offset, end.offset, fraction)
        }

        override suspend fun animateTo(
            scope: CoroutineScope,
            props: Props,
            springSpec: SpringSpec<Float>,
            onStart: () -> Unit,
            onFinished: () -> Unit
        ) {
            scope.launch {
                onStart()
                listOf(
                    scope.async {
                        offset.animateTo(
                            props.offset.value,
                            spring(springSpec.dampingRatio, springSpec.stiffness)
                        )
                    }
                ).awaitAll()
                onFinished()
            }
        }
    }

    override fun defaultProps(): Props = Props(
        offset = OffsetP(DpOffset.Zero).also {
            it.displacement = derivedStateOf {
                DpOffset((scroll.value * width.value).dp, 0.dp)
            }
        }
    )

    private val created = Props(
        offset = OffsetP(DpOffset(0.dp, 500.dp)),
        scale = 0f,
        alpha = 1f,
        zIndex = 0f,
        aspectRatio = 1f,
        isVisible = false
    )

    private val standard = Props(
        offset = OffsetP(DpOffset.Zero),
        isVisible = true
    )

    private val destroyed = Props(
        offset = OffsetP(DpOffset(0.dp, (-500).dp)),
        scale = 0f,
        alpha = 0f,
        zIndex = -1f,
        aspectRatio = 1f,
        rotation = 360f,
        isVisible = false
    )

    override fun snapGeometry(output: TransitionModel.Output<SpotlightModel.State<NavTarget>>) {
        val target = targetIndex(output)
        scroll.snapTo(target)
    }

    override fun animateGeometry(output: TransitionModel.Output<SpotlightModel.State<NavTarget>>) {
        val target = targetIndex(output)

        scroll.animateTo(
            target,
            // FIXME animation spec should come from client code
            spring()
        )
    }

    // TODO make this work in a generic way: List<(ModelState) -> Animatable>
    private fun targetIndex(output: TransitionModel.Output<SpotlightModel.State<NavTarget>>) =
        when (output) {
            is Keyframes -> Interpolator.lerpFloat(
                output.currentSegment.fromState.activeIndex,
                output.currentSegment.targetState.activeIndex,
                output.segmentProgress
            )

            is Update -> output.currentTargetState.activeIndex
        }

    override fun SpotlightModel.State<NavTarget>.toProps(): List<MatchedProps<NavTarget, Props>> {
        return positions.flatMapIndexed { index, position ->
            position.elements.map {
                val target = it.value.toProps()
                MatchedProps(
                    element = it.key,
                    props = Props(
                        offset = OffsetP(
                            DpOffset(
                                dpOffset(index).x,
                                target.offset.value.y
                            )
                        ),
                        scale = target.scale,
                        alpha = target.alpha,
                        zIndex = target.zIndex,
                        rotation = target.rotation,
                        aspectRatio = target.aspectRatio,
                        isVisible = (index + activeWindow.toInt()) <= activeIndex || (index - activeWindow.toInt()) >= activeIndex
                    )
                )
            }
        }
    }

    fun SpotlightModel.State.ElementState.toProps() =
        when (this) {
            CREATED -> created
            STANDARD -> standard
            DESTROYED -> destroyed
        }

    private fun dpOffset(
        index: Int
    ) = DpOffset(
        x = (index * width.value).dp,
        y = 0.dp
    )

    class Gestures<NavTarget>(
        transitionBounds: TransitionBounds,
        private val orientation: Orientation = Orientation.Horizontal, // TODO support RTL
    ) : GestureFactory<NavTarget, SpotlightModel.State<NavTarget>> {
        private val width = transitionBounds.widthPx
        private val height = transitionBounds.heightPx

        override fun createGesture(
            delta: Offset,
            density: Density
        ): Gesture<NavTarget, SpotlightModel.State<NavTarget>> {
            return when (orientation) {
                Orientation.Horizontal -> if (delta.x < 0) {
                    Gesture(
                        operation = Next(KEYFRAME),
                        dragToProgress = { offset -> (offset.x / width) * -1 },
                        partial = { offset, progress -> offset.copy(x = progress * width * -1) }
                    )
                } else {
                    Gesture(
                        operation = Previous(KEYFRAME),
                        dragToProgress = { offset -> (offset.x / width) },
                        partial = { offset, partial -> offset.copy(x = partial * width) }
                    )
                }
                Orientation.Vertical -> if (delta.y < 0) {
                    Gesture(
                        operation = Next(KEYFRAME),
                        dragToProgress = { offset -> (offset.y / height) * -1 },
                        partial = { offset, partial -> offset.copy(y = partial * height * -1) }
                    )
                } else {
                    Gesture(
                        operation = Previous(KEYFRAME),
                        dragToProgress = { offset -> (offset.y / height) },
                        partial = { offset, partial -> offset.copy(y = partial * height) }
                    )
                }
            }
        }
    }


    operator fun DpOffset.times(multiplier: Int) =
        DpOffset(x * multiplier, y * multiplier)

}

