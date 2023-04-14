package com.bumble.appyx.transitionmodel.spotlight.ui.fader


// TODO BaseMotionController + dynamic on visibility calculation
//class SpotlightFader<InteractionTarget : Any>(
//    transitionParams: TransitionParams
//) : Interpolator<InteractionTarget, SpotlightModel.State<InteractionTarget>> {
//
//    class Props(
//        val alpha: Alpha,
//        override val isVisible: Boolean
//    ) : BaseProps
//
//    private val visible = Props(
//        alpha = Alpha(1f),
//        isVisible = true
//    )
//
//    private val hidden = Props(
//        alpha = Alpha(0f),
//        isVisible = false
//    )
//
//    // TODO Migrate to BaseMotionController
//
//    fun SpotlightModel.State.ElementState.isVisible() =
//        when (this) {
//            CREATED -> false
//            STANDARD -> true
//            DESTROYED -> false
//        }
//
//    private fun <InteractionTarget> SpotlightModel.State<InteractionTarget>.toProps(): List<MatchedProps<InteractionTarget, SpotlightFader.Props>> {
//        return positions.flatMapIndexed { index, position ->
//            position.elements.map {
//                val isVisible =
//                    it.value.isVisible() && (activeIndex - activeWindow / 2 <= index && index <= activeIndex + activeWindow / 2)
//                val target = if (isVisible) visible else hidden
//                MatchedProps(
//                    element = it.key,
//                    props = Props(
//                        alpha = target.alpha,
//                        isVisible = true
//                    )
//                )
//            }
//        }
//    }
//
//    override fun mapSegment(
//        segment: Segment<SpotlightModel.State<InteractionTarget>>,
//        segmentProgress: Float
//    ): List<FrameModel<InteractionTarget>> {
//        val (fromState, targetState) = segment.navTransition
//        val fromProps = fromState.toProps()
//        val targetProps = targetState.toProps()
//
//        // TODO: use a map instead of find
//        return targetProps.map { t1 ->
//            val t0 = fromProps.find { it.element.id == t1.element.id }!!
//            val alpha = lerpFloat(t0.props.alpha.value, t1.props.alpha.value, segmentProgress)
//
//            FrameModel(
//                navElement = t1.element,
//                modifier = Modifier
//                    .alpha(alpha),
//                progress = segmentProgress,
//                state = resolveNavElementVisibility(t0.props, t1.props, segmentProgress)
//            )
//        }
//    }
//
//    override fun mapUpdate(update: Update<SpotlightModel.State<InteractionTarget>>): List<FrameModel<InteractionTarget>> {
//        TODO("Not yet implemented")
//    }
//}
