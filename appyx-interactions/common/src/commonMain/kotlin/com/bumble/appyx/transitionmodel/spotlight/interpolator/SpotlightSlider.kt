package com.bumble.appyx.transitionmodel.spotlight.interpolator

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.TransitionModel
import com.bumble.appyx.interactions.core.inputsource.Gesture
import com.bumble.appyx.interactions.core.ui.FrameModel
import com.bumble.appyx.interactions.core.ui.GestureFactory
import com.bumble.appyx.interactions.core.ui.Interpolator
import com.bumble.appyx.interactions.core.ui.Interpolator.Companion.lerpDpOffset
import com.bumble.appyx.interactions.core.ui.Interpolator.Companion.lerpFloat
import com.bumble.appyx.interactions.core.ui.MatchedProps
import com.bumble.appyx.interactions.core.ui.TransitionBounds
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel
import com.bumble.appyx.transitionmodel.spotlight.operation.Next
import com.bumble.appyx.transitionmodel.spotlight.operation.Previous

class SpotlightSlider<NavTarget>(
    transitionBounds: TransitionBounds,
    private val orientation: Orientation = Orientation.Horizontal, // TODO support RTL
) : Interpolator<NavTarget, SpotlightModel.State<NavTarget>> {
    private val width = transitionBounds.widthDp
    private val height = transitionBounds.heightDp

    data class Props(
        val offset: DpOffset,
        val scale: Float = 1f,
        val alpha: Float = 1f
    )

//    private val horizontal = Props(
//        offset = DpOffset(width, 0.dp)
//    )
//
//    private val vertical = Props(
//        offset = DpOffset(0.dp, height)
//    )

    private fun <NavTarget> SpotlightModel.State<NavTarget>.toProps(): List<MatchedProps<NavTarget, Props>> =
        created.mapIndexed { index, navElement ->
            MatchedProps(
                navElement, Props(
                    offset = dpOffset(index),
                    scale = 0f,
                    alpha = 1f
                )
            )
        } +
                standard.mapIndexed { index, navElement ->
                    MatchedProps(
                        navElement, Props(
                            offset = dpOffset(index),
                            scale = 1f
                        )
                    )
                } +
                destroyed.mapIndexed { index, navElement ->
                    MatchedProps(
                        navElement, Props(
                            offset = dpOffset(index),
                            scale = 2f,
                            alpha = 0f
                        )
                    )
                }

    private fun <NavTarget> SpotlightModel.State<NavTarget>.dpOffset(
        index: Int
    ) = DpOffset(
        x = ((index - this.activeIndex) * width.value).dp,
        y = 0.dp
    )

    override fun map(segment: TransitionModel.Segment<SpotlightModel.State<NavTarget>>): List<FrameModel<NavTarget>> {
        val (fromState, targetState) = segment.navTransition
        val fromProps = fromState.toProps()
        val targetProps = targetState.toProps()

        return targetProps.map { t1 ->
            val t0 = fromProps.find { it.element.id == t1.element.id }!!

            val alpha = lerpFloat(t0.props.alpha, t1.props.alpha, segment.progress)
            val scale = lerpFloat(t0.props.scale, t1.props.scale, segment.progress)
            val offset = lerpDpOffset(t0.props.offset, t1.props.offset, segment.progress)

            FrameModel(
                navElement = t1.element,
                modifier = Modifier
                    .alpha(alpha)
                    .scale(scale)
                    .offset(
                        x = offset.x,
                        y = offset.y
                    ),
                progress = segment.progress
            )
        }
    }

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
                        operation = Next(),
                        dragToProgress = { offset -> (offset.x / width) * -1 },
                        partial = { offset, progress -> offset.copy(x = progress * width * -1) }
                    )
                } else {
                    Gesture(
                        operation = Previous(),
                        dragToProgress = { offset -> (offset.x / width) },
                        partial = { offset, partial -> offset.copy(x = partial * width) }
                    )
                }
                Orientation.Vertical -> if (delta.y < 0) {
                    Gesture(
                        operation = Next(),
                        dragToProgress = { offset -> (offset.y / height) * -1 },
                        partial = { offset, partial -> offset.copy(y = partial * height * -1) }
                    )
                } else {
                    Gesture(
                        operation = Previous(),
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

