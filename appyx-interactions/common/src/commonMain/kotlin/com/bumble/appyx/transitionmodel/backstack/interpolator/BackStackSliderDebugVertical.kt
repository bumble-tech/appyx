package com.bumble.appyx.transitionmodel.backstack.interpolator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.TransitionModel.Segment
import com.bumble.appyx.interactions.core.ui.RenderParams
import com.bumble.appyx.interactions.core.ui.UiProps
import com.bumble.appyx.transitionmodel.backstack.BackStack
import com.bumble.appyx.transitionmodel.backstack.BackStack.State.DROPPED
import com.bumble.appyx.transitionmodel.backstack.BackStack.State.POPPED
import com.bumble.appyx.transitionmodel.backstack.BackStack.State.REPLACED
import com.bumble.appyx.transitionmodel.backstack.BackStack.State.STASHED
import androidx.compose.ui.graphics.lerp as lerpGraphics
import androidx.compose.ui.unit.lerp as lerpUnit

class BackStackSliderDebugVertical<NavTarget> : UiProps<NavTarget, BackStack.State> {
    private val size = 100.dp

    class Props(
        val offset: DpOffset,
        val color: Color,
    )

    private val created = Props(
        offset = DpOffset(0.dp, 0.dp),
        color = Color.Green,
    )

    private val active = Props(
        offset = DpOffset(0.dp, size),
        color = Color.Blue, // (0x550000FF),
    )

    private fun BackStack.State.toProps(stashIndex: Int, popIndex: Int, dropIndex: Int): Props =
        when (this) {
            BackStack.State.CREATED -> created
            BackStack.State.ACTIVE -> active
            POPPED -> Props(
                offset = DpOffset(0.dp, ((popIndex) * -size.value).dp),
                color = Color.Red,
            )
            STASHED -> Props(
                offset = DpOffset(0.dp, ((stashIndex + 1) * size.value).dp),
                color = Color.Yellow,
            )
            REPLACED -> Props(
                offset = DpOffset(size, size),
                color = Color.Magenta,
            )
            DROPPED -> Props(
                offset = DpOffset(0.dp, ((dropIndex + 1) * size.value).dp),
                color = Color.DarkGray,
            )
        }

    override fun map(segment: Segment<NavTarget, BackStack.State>): List<RenderParams<NavTarget, BackStack.State>> {
        val fromState = segment.navTransition.fromState
        val targetState = segment.navTransition.targetState
        val fromStashed = fromState.filter { it.state == STASHED }
        val targetStashed = targetState.filter { it.state == STASHED }
        val fromPopped = fromState.filter { it.state == POPPED }
        val targetPopped = targetState.filter { it.state == POPPED }
        val fromDropped = fromState.filter { it.state == DROPPED }
        val targetDropped = targetState.filter { it.state == DROPPED }

        return targetState.mapIndexed { index, t1 ->
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
            val offset = lerpUnit(fromProps.offset, targetProps.offset, segment.progress)
            val color = lerpGraphics(fromProps.color, targetProps.color, segment.progress)

            RenderParams(
                navElement = t1,
                modifier = Modifier
                    .offset(x = offset.x, y = offset.y)
                    .background(color)
            )
        }
    }
}
