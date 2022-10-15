package com.bumble.appyx.navmodel2.backstack.transitionhandler

import androidx.compose.foundation.layout.offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.core.navigation.transition.TransitionParams
import com.bumble.appyx.core.navigation2.NavModel.Segment
import com.bumble.appyx.core.navigation2.ui.Modifiers
import com.bumble.appyx.core.navigation2.ui.UiProps
import com.bumble.appyx.navmodel2.backstack.BackStack
import com.bumble.appyx.navmodel2.backstack.BackStack.State.ACTIVE
import com.bumble.appyx.navmodel2.backstack.BackStack.State.CREATED
import com.bumble.appyx.navmodel2.backstack.BackStack.State.DROPPED
import com.bumble.appyx.navmodel2.backstack.BackStack.State.POPPED
import com.bumble.appyx.navmodel2.backstack.BackStack.State.REPLACED
import com.bumble.appyx.navmodel2.backstack.BackStack.State.STASHED
import androidx.compose.ui.unit.lerp as lerpUnit

class BackStackSlider<NavTarget>(
    transitionParams: TransitionParams
) : UiProps<NavTarget, BackStack.State> {
    private val width = transitionParams.bounds.width
    private val height = transitionParams.bounds.height

    data class Props(
        val offset: DpOffset,
        val offsetMultiplier: Int = 1
    )

    private val outsideLeft = Props(
        offset = DpOffset(-width, 0.dp)
    )

    private val outsideRight = Props(
        offset = DpOffset(width, 0.dp)
    )

    private val outsideTop = Props(
        offset = DpOffset(0.dp, -height)
    )

    private val noOffset = Props(
        offset = DpOffset(0.dp, 0.dp)
    )

    // FIXME single Int, based on relative position to ACTIVE element
    private fun BackStack.State.toProps(stashIndex: Int, popIndex: Int, dropIndex: Int): Props =
        when (this) {
            ACTIVE -> noOffset
            CREATED -> outsideRight
            REPLACED -> outsideTop
            POPPED -> outsideRight.copy(offsetMultiplier = popIndex + 1)
            STASHED -> outsideLeft.copy(offsetMultiplier = stashIndex)
            DROPPED -> outsideLeft.copy(offsetMultiplier = dropIndex + 1)
        }

    override fun map(segment: Segment<NavTarget, BackStack.State>): List<Modifiers<NavTarget, BackStack.State>> {
        val (fromState, targetState) = segment.navTransition

        // TODO memoize per segment, as only percentage will change
        val fromStashed = fromState.filter { it.state == STASHED }
        val targetStashed = targetState.filter { it.state == STASHED }
        val fromPopped = fromState.filter { it.state == POPPED }
        val targetPopped = targetState.filter { it.state == POPPED }
        val fromDropped = fromState.filter { it.state == DROPPED }
        val targetDropped = targetState.filter { it.state == DROPPED }

        return targetState.mapIndexed { index, t1 ->
            // TODO memoize per segment, as only percentage will change
            val t0 = fromState.find { it.key == t1.key }!!
            val fromStashIndex = fromStashed.size - fromStashed.indexOf(t0)
            val targetStashIndex = targetStashed.size - targetStashed.indexOf(t1)
            val fromPoppedIndex = fromPopped.indexOf(t0)
            val targetPoppedIndex = targetPopped.indexOf(t1)
            val fromDroppedIndex = fromDropped.size - fromDropped.indexOf(t0)
            val targetDroppedIndex = targetDropped.size - targetDropped.indexOf(t1)

            val fromProps = t0.state.toProps(fromStashIndex, fromPoppedIndex, fromDroppedIndex)
            val targetProps = t1.state.toProps(targetStashIndex, targetPoppedIndex, targetDroppedIndex)
            val offset = lerpUnit(
                start = fromProps.offset * fromProps.offsetMultiplier,
                stop = targetProps.offset * targetProps.offsetMultiplier,
                fraction = segment.progress
            )

            Modifiers(
                navElement = t1,
                modifier = Modifier.offset(
                    x = offset.x,
                    y = offset.y)
            )
        }
    }

    operator fun DpOffset.times(multiplier: Int) =
        DpOffset(x * multiplier, y * multiplier)

}
