package com.bumble.appyx.transitionmodel.backstack.interpolator

import androidx.compose.foundation.layout.offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.TransitionModel
import com.bumble.appyx.interactions.core.ui.FrameModel
import com.bumble.appyx.interactions.core.ui.Interpolator
import com.bumble.appyx.interactions.core.ui.TransitionBounds
import com.bumble.appyx.transitionmodel.backstack.BackStackModel
import com.bumble.appyx.transitionmodel.backstack.BackStackModel.State.ACTIVE
import com.bumble.appyx.transitionmodel.backstack.BackStackModel.State.CREATED
import com.bumble.appyx.transitionmodel.backstack.BackStackModel.State.DROPPED
import com.bumble.appyx.transitionmodel.backstack.BackStackModel.State.POPPED
import com.bumble.appyx.transitionmodel.backstack.BackStackModel.State.REPLACED
import com.bumble.appyx.transitionmodel.backstack.BackStackModel.State.STASHED
import androidx.compose.ui.unit.lerp as lerpUnit

class BackStackSlider<NavTarget>(
    transitionBounds: TransitionBounds
) : Interpolator<NavTarget, BackStackModel.State> {
    private val width = transitionBounds.widthDp
    private val height = transitionBounds.heightDp

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
    private fun BackStackModel.State.toProps(
        stashIndex: Int,
        popIndex: Int,
        dropIndex: Int
    ): Props =
        when (this) {
            ACTIVE -> noOffset
            CREATED -> outsideRight
            REPLACED -> outsideTop
            POPPED -> outsideRight.copy(offsetMultiplier = popIndex + 1)
            STASHED -> outsideLeft.copy(offsetMultiplier = stashIndex)
            DROPPED -> outsideLeft.copy(offsetMultiplier = dropIndex + 1)
        }

    override fun map(segment: TransitionModel.Segment<NavTarget, BackStackModel.State>): List<FrameModel<NavTarget, BackStackModel.State>> {
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
            val targetProps =
                t1.state.toProps(targetStashIndex, targetPoppedIndex, targetDroppedIndex)
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

    operator fun DpOffset.times(multiplier: Int) =
        DpOffset(x * multiplier, y * multiplier)

}
