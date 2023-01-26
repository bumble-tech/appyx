package com.bumble.appyx.transitionmodel.backstack.interpolator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.TransitionModel
import com.bumble.appyx.interactions.core.ui.FrameModel
import com.bumble.appyx.interactions.core.ui.Interpolator
import com.bumble.appyx.interactions.core.ui.MatchedProps
import com.bumble.appyx.interactions.core.ui.VisibilityInterpolator
import com.bumble.appyx.transitionmodel.backstack.BackStackModel

class BackStackSliderDebugVertical<NavTarget>
    : Interpolator<NavTarget, BackStackModel.State<NavTarget>>,
    VisibilityInterpolator<NavTarget, BackStackModel.State<NavTarget>> by BackStackVisibilityInterpolator() {
    private val size = 100.dp


    data class Props(
        val offset: DpOffset,
        val color: Color
    )

    private val createdProps = Props(
        offset = DpOffset(0.dp, 0.dp),
        color = Color.Green,
    )

    private val activeProps = Props(
        offset = DpOffset(0.dp, size),
        color = Color.Blue,
    )

    private val stashedProps = activeProps.copy(
        color = Color.Yellow,
    )

    private val destroyedProps = activeProps.copy(
        color = Color.DarkGray,
    )

    private fun <NavTarget> BackStackModel.State<NavTarget>.toProps(): List<MatchedProps<NavTarget, Props>> =
        created.map { MatchedProps(it, createdProps) } +
                listOf(MatchedProps(active, activeProps)) +
                stashed.mapIndexed { index, navElement ->
                    MatchedProps(
                        navElement,
                        stashedProps.copy(
                            offset = DpOffset(0.dp, ((index + 1) * size.value).dp)
                        )
                    )
                } +
                destroyed.mapIndexed { index, navElement ->
                    MatchedProps(
                        navElement,
                        destroyedProps.copy(
                            offset = DpOffset(0.dp, ((index + 1) * size.value).dp)
                        )
                    )
                }


    override fun map(segment: TransitionModel.Segment<BackStackModel.State<NavTarget>>): List<FrameModel<NavTarget>> {
        val (fromState, targetState) = segment.navTransition
        val fromProps = fromState.toProps()
        val targetProps = targetState.toProps()

        return targetProps.map { t1 ->
            val t0 = fromProps.find { it.element.id == t1.element.id }!!

            val offset = Interpolator.lerpDpOffset(
                start = t0.props.offset,
                end = t1.props.offset,
                progress = segment.progress
            )

            val color = lerp(t0.props.color, t1.props.color, segment.progress)

            FrameModel(
                navElement = t1.element,
                modifier = Modifier
                    .offset(
                        x = offset.x,
                        y = offset.y
                    )
                    .background(color = color),
                progress = segment.progress
            )
        }

    }
}

//
//    private fun BackStackModel.State.toProps(
//        stashIndex: Int,
//        popIndex: Int,
//        dropIndex: Int
//    ): Props =
//        when (this) {
//            BackStackModel.State.CREATED -> created
//            BackStackModel.State.ACTIVE -> active
//            POPPED -> Props(
//                offset = DpOffset(0.dp, ((popIndex) * -size.value).dp),
//                color = Color.Red,
//            )
//            STASHED -> Props(
//                offset = DpOffset(0.dp, ((stashIndex + 1) * size.value).dp),
//                color = Color.Yellow,
//            )
//            REPLACED -> Props(
//                offset = DpOffset(size, size),
//                color = Color.Magenta,
//            )
//            DROPPED -> Props(
//                offset = DpOffset(0.dp, ((dropIndex + 1) * size.value).dp),
//                color = Color.DarkGray,
//            )
//        }
//
//    override fun map(segment: Segment<NavTarget, BackStackModel.State>): List<FrameModel<NavTarget, BackStackModel.State>> {
//        val fromState = segment.navTransition.fromState
//        val targetState = segment.navTransition.targetState
////        val fromStashed = fromState.filter { it.state == STASHED }
////        val targetStashed = targetState.filter { it.state == STASHED }
////        val fromPopped = fromState.filter { it.state == POPPED }
////        val targetPopped = targetState.filter { it.state == POPPED }
////        val fromDropped = fromState.filter { it.state == DROPPED }
////        val targetDropped = targetState.filter { it.state == DROPPED }
//
//        return targetState.mapIndexed { index, t1 ->
//            val t0 = fromState.find { it.key == t1.key }!!
//            val fromStashIndex = fromStashed.size - fromStashed.indexOf(t0)
//            val targetStashIndex = targetStashed.size - targetStashed.indexOf(t1)
//            val fromPoppedIndex = fromPopped.indexOf(t0)
//            val targetPoppedIndex = targetPopped.indexOf(t1)
//            val fromDroppedIndex = fromDropped.size - fromDropped.indexOf(t0)
//            val targetDroppedIndex = targetDropped.size - targetDropped.indexOf(t1)
//
//            val fromProps = t0.state.toProps(fromStashIndex, fromPoppedIndex, fromDroppedIndex)
//            val targetProps =
//                t1.state.toProps(targetStashIndex, targetPoppedIndex, targetDroppedIndex)
//            val offset = lerpUnit(fromProps.offset, targetProps.offset, segment.progress)
//            val color = lerpGraphics(fromProps.color, targetProps.color, segment.progress)
//
//            FrameModel(
//                navElement = t1,
//                modifier = Modifier
//                    .offset(x = offset.x, y = offset.y)
//                    .background(color),
//                progress = segment.progress
//            )
//        }
//    }
//}
