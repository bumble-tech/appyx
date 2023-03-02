package com.bumble.appyx.transitionmodel.backstack.interpolator

// TODO discard or convert to BaseMotionController
//class BackStackSliderDebugVertical<InteractionTarget>
//    : Interpolator<InteractionTarget, BackStackModel.State<InteractionTarget>> {
//    private val size = 100.dp
//
//
//    data class Props(
//        val offset: DpOffset,
//        val color: Color,
//        // TODO
//        override val isVisible: Boolean
//    ): BaseProps
//
//    private val createdProps = Props(
//        offset = DpOffset(0.dp, 0.dp),
//        color = Color.Green,
//        isVisible = false
//    )
//
//    private val activeProps = Props(
//        offset = DpOffset(0.dp, size),
//        color = Color.Blue,
//        isVisible = true
//    )
//
//    private val stashedProps = activeProps.copy(
//        color = Color.Yellow,
//        isVisible = false
//    )
//
//    private val destroyedProps = activeProps.copy(
//        color = Color.DarkGray,
//        isVisible = false
//    )
//
//    private fun <InteractionTarget> BackStackModel.State<InteractionTarget>.toProps(): List<MatchedProps<InteractionTarget, Props>> =
//        created.map { MatchedProps(it, createdProps) } +
//                listOf(MatchedProps(active, activeProps)) +
//                stashed.mapIndexed { index, navElement ->
//                    MatchedProps(
//                        navElement,
//                        stashedProps.copy(
//                            offset = DpOffset(0.dp, ((index + 1) * size.value).dp)
//                        )
//                    )
//                } +
//                destroyed.mapIndexed { index, navElement ->
//                    MatchedProps(
//                        navElement,
//                        destroyedProps.copy(
//                            offset = DpOffset(0.dp, ((index + 1) * size.value).dp)
//                        )
//                    )
//                }
//
//
//    override fun mapSegment(segment: Segment<BackStackModel.State<InteractionTarget>>, segmentProgress: Float): List<FrameModel<InteractionTarget>> {
//        val (fromState, targetState) = segment.navTransition
//        val fromProps = fromState.toProps()
//        val targetProps = targetState.toProps()
//
//        return targetProps.map { t1 ->
//            val t0 = fromProps.find { it.element.id == t1.element.id }!!
//
//            val offset = Interpolator.lerpDpOffset(
//                start = t0.props.offset,
//                end = t1.props.offset,
//                progress = segmentProgress
//            )
//
//            val color = lerp(t0.props.color, t1.props.color, segmentProgress)
//
//            FrameModel(
//                navElement = t1.element,
//                modifier = Modifier
//                    .offset(
//                        x = offset.x,
//                        y = offset.y
//                    )
//                    .background(color = color),
//                progress = segmentProgress,
//                state = resolveNavElementVisibility(t0.props, t1.props, segmentProgress)
//            )
//        }
//    }
//
//    // TODO Migrate to BaseMotionController
//
//    override fun mapUpdate(update: Update<BackStackModel.State<InteractionTarget>>): List<FrameModel<InteractionTarget>> {
//        TODO("Not yet implemented")
//    }
//}
//
////
////    private fun BackStackModel.State.toProps(
////        stashIndex: Int,
////        popIndex: Int,
////        dropIndex: Int
////    ): Props =
////        when (this) {
////            BackStackModel.State.CREATED -> created
////            BackStackModel.State.ACTIVE -> active
////            POPPED -> Props(
////                offset = DpOffset(0.dp, ((popIndex) * -size.value).dp),
////                color = Color.Red,
////            )
////            STASHED -> Props(
////                offset = DpOffset(0.dp, ((stashIndex + 1) * size.value).dp),
////                color = Color.Yellow,
////            )
////            REPLACED -> Props(
////                offset = DpOffset(size, size),
////                color = Color.Magenta,
////            )
////            DROPPED -> Props(
////                offset = DpOffset(0.dp, ((dropIndex + 1) * size.value).dp),
////                color = Color.DarkGray,
////            )
////        }
////
////    override fun map(segment: Segment<InteractionTarget, BackStackModel.State>): List<FrameModel<InteractionTarget, BackStackModel.State>> {
////        val fromState = segment.navTransition.fromState
////        val targetState = segment.navTransition.targetState
//////        val fromStashed = fromState.filter { it.state == STASHED }
//////        val targetStashed = targetState.filter { it.state == STASHED }
//////        val fromPopped = fromState.filter { it.state == POPPED }
//////        val targetPopped = targetState.filter { it.state == POPPED }
//////        val fromDropped = fromState.filter { it.state == DROPPED }
//////        val targetDropped = targetState.filter { it.state == DROPPED }
////
////        return targetState.mapIndexed { index, t1 ->
////            val t0 = fromState.find { it.key == t1.key }!!
////            val fromStashIndex = fromStashed.size - fromStashed.indexOf(t0)
////            val targetStashIndex = targetStashed.size - targetStashed.indexOf(t1)
////            val fromPoppedIndex = fromPopped.indexOf(t0)
////            val targetPoppedIndex = targetPopped.indexOf(t1)
////            val fromDroppedIndex = fromDropped.size - fromDropped.indexOf(t0)
////            val targetDroppedIndex = targetDropped.size - targetDropped.indexOf(t1)
////
////            val fromProps = t0.state.toProps(fromStashIndex, fromPoppedIndex, fromDroppedIndex)
////            val targetProps =
////                t1.state.toProps(targetStashIndex, targetPoppedIndex, targetDroppedIndex)
////            val offset = lerpUnit(fromProps.offset, targetProps.offset, segmentProgress)
////            val color = lerpGraphics(fromProps.color, targetProps.color, segmentProgress)
////
////            FrameModel(
////                navElement = t1,
////                modifier = Modifier
////                    .offset(x = offset.x, y = offset.y)
////                    .background(color),
////                progress = segmentProgress
////            )
////        }
////    }
////}
