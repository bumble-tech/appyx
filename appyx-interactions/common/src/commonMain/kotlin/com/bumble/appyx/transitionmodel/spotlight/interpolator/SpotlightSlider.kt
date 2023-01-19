package com.bumble.appyx.transitionmodel.spotlight.interpolator

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.TransitionModel
import com.bumble.appyx.interactions.core.inputsource.Gesture
import com.bumble.appyx.interactions.core.ui.FrameModel
import com.bumble.appyx.interactions.core.ui.GestureFactory
import com.bumble.appyx.interactions.core.ui.Interpolator
import com.bumble.appyx.interactions.core.ui.TransitionBounds
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel.State.ACTIVE
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel.State.INACTIVE_AFTER
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel.State.INACTIVE_BEFORE
import com.bumble.appyx.transitionmodel.spotlight.operation.Next
import com.bumble.appyx.transitionmodel.spotlight.operation.Previous
import androidx.compose.ui.unit.lerp as lerpUnit

class SpotlightSlider<NavTarget>(
    transitionBounds: TransitionBounds,
    private val orientation: Orientation = Orientation.Horizontal, // TODO support RTL
) : Interpolator<NavTarget, SpotlightModel.State> {
    private val width = transitionBounds.widthDp
    private val height = transitionBounds.heightDp

    data class Props(
        val offset: DpOffset,
        val offsetMultiplier: Int = 1
    )

    private val top = Props(
        offset = DpOffset(0.dp, -height)
    )

    private val bottom = Props(
        offset = DpOffset(0.dp, height)
    )

    private val left = Props(
        offset = DpOffset(-width, 0.dp)
    )

    private val right = Props(
        offset = DpOffset(width, 0.dp)
    )

    private val center = Props(
        offset = DpOffset(0.dp, 0.dp)
    )

    // FIXME single Int, based on relative position to ACTIVE element
    private fun SpotlightModel.State.toProps(beforeIndex: Int, afterIndex: Int): Props =
        when (this) {
            ACTIVE -> center
            INACTIVE_BEFORE -> when (orientation) {
                Orientation.Horizontal -> left.copy(offsetMultiplier = beforeIndex)
                Orientation.Vertical -> top.copy(offsetMultiplier = beforeIndex)
            }

            INACTIVE_AFTER -> when (orientation) {
                Orientation.Horizontal -> right.copy(offsetMultiplier = afterIndex + 1)
                Orientation.Vertical -> bottom.copy(offsetMultiplier = afterIndex + 1)
            }
        }

    override fun map(segment: TransitionModel.Segment<NavTarget, SpotlightModel.State>): List<FrameModel<NavTarget, SpotlightModel.State>> {
        val (fromState, targetState) = segment.navTransition

        // TODO memoize per segment, as only percentage will change
        val fromBefore = fromState.filter { it.state == INACTIVE_BEFORE }
        val targetBefore = targetState.filter { it.state == INACTIVE_BEFORE }
        val fromAfter = fromState.filter { it.state == INACTIVE_AFTER }
        val targetAfter = targetState.filter { it.state == INACTIVE_AFTER }

        return targetState.mapIndexed { index, t1 ->
            // TODO memoize per segment, as only percentage will change
            val t0 = fromState.find { it.key == t1.key }
            require(t0 != null)
            val fromBeforeIndex = fromBefore.size - fromBefore.indexOf(t0)
            val targetBeforeIndex = targetBefore.size - targetBefore.indexOf(t1)
            val fromAfterIndex = fromAfter.indexOf(t0)
            val targetAfterIndex = targetAfter.indexOf(t1)


            val fromProps = t0.state.toProps(fromBeforeIndex, fromAfterIndex)
            val targetProps = t1.state.toProps(targetBeforeIndex, targetAfterIndex)
            val offset = lerpUnit(
                start = fromProps.offset * fromProps.offsetMultiplier,
                stop = targetProps.offset * targetProps.offsetMultiplier,
                fraction = segment.progress
            )

            FrameModel(
                navElement = t1,
                modifier = Modifier.offset(
                    x = offset.x,
                    y = offset.y
                ),
                progress = segment.progress
            )
        }
    }

//    fun calculateProgressForGesture(delta: Offset, density: Density): Float {
//        // FIXME Log.d("calculateProgress", "${delta.x} / $width = ${delta.x / width}")
//        return when (orientation) {
//            Orientation.Horizontal -> delta.x / width * -1
//            Orientation.Vertical -> delta.y / height * -1
//        }
//    }

    class Gestures<NavTarget>(
        transitionBounds: TransitionBounds,
        private val orientation: Orientation = Orientation.Horizontal, // TODO support RTL
    ) : GestureFactory<NavTarget, SpotlightModel.State> {
        private val width = transitionBounds.widthPx
        private val height = transitionBounds.heightPx

        override fun createGesture(
            delta: Offset,
            density: Density
        ): Gesture<NavTarget, SpotlightModel.State> {
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

