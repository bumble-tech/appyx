package com.bumble.appyx.navmodel2.spotlight.transitionhandler

import android.util.Log
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.core.navigation.transition.TransitionParams
import com.bumble.appyx.core.navigation2.NavModel.State
import com.bumble.appyx.core.navigation2.inputsource.Gesture
import com.bumble.appyx.core.navigation2.ui.Modifiers
import com.bumble.appyx.core.navigation2.ui.UiProps
import com.bumble.appyx.navmodel2.spotlight.Spotlight
import com.bumble.appyx.navmodel2.spotlight.Spotlight.State.ACTIVE
import com.bumble.appyx.navmodel2.spotlight.Spotlight.State.INACTIVE_AFTER
import com.bumble.appyx.navmodel2.spotlight.Spotlight.State.INACTIVE_BEFORE
import com.bumble.appyx.navmodel2.spotlight.operation.Next
import com.bumble.appyx.navmodel2.spotlight.operation.Previous
import androidx.compose.ui.unit.lerp as lerpUnit

class SpotlightSlider<NavTarget>(
    transitionParams: TransitionParams,
    private val orientation: Orientation = Orientation.Horizontal, // TODO support RTL
) : UiProps<NavTarget, Spotlight.State> {
    private val width = transitionParams.bounds.width
    private val height = transitionParams.bounds.height

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
    private fun Spotlight.State.toProps(beforeIndex: Int, afterIndex: Int): Props =
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

    override fun map(segment: State<NavTarget, Spotlight.State>): List<Modifiers<NavTarget, Spotlight.State>> {
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

            Modifiers(
                navElement = t1,
                modifier = Modifier.offset(
                    x = offset.x,
                    y = offset.y
                )
            )
        }
    }

    // TODO Modify TransitionParams to also contain width & height in px, not just dp
    fun calculateProgressForGesture(delta: Offset, density: Density): Float {
        val width = with(density) { width.toPx() }
        val height = with(density) { height.toPx() }

        Log.d("calculateProgress", "${delta.x} / $width = ${delta.x / width}")
        return when (orientation) {
            Orientation.Horizontal -> delta.x / width * -1
            Orientation.Vertical -> delta.y / height * -1
        }
    }

    fun createGesture(delta: Offset, density: Density): Gesture<NavTarget, Spotlight.State> {
        val width = with(density) { width.toPx() }
        val height = with(density) { height.toPx() }

        return when (orientation) {
            Orientation.Horizontal -> if (delta.x < 0) {
                Gesture(Next()) { offset -> (offset.x / width) * -1 }
            } else {
                Gesture(Previous()) { offset -> (offset.x / width) }
            }
            Orientation.Vertical -> if (delta.y < 0) {
                Gesture(Next()) { offset -> (offset.y / height) * -1 }
            } else {
                Gesture(Previous()) { offset -> (offset.y / height) }
            }
        }
    }

    operator fun DpOffset.times(multiplier: Int) =
        DpOffset(x * multiplier, y * multiplier)

}

