package com.bumble.appyx.transitionmodel.spotlight.interpolator


// TODO BaseInterpolator + dynamic on visibility calculation
//class SpotlightFader<NavTarget : Any>(
//    transitionParams: TransitionParams
//) : Interpolator<NavTarget, SpotlightModel.State<NavTarget>> {
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
//    // TODO Migrate to BaseInterpolator
//
//    fun SpotlightModel.State.ElementState.isVisible() =
//        when (this) {
//            CREATED -> false
//            STANDARD -> true
//            DESTROYED -> false
//        }
//
//    private fun <NavTarget> SpotlightModel.State<NavTarget>.toProps(): List<MatchedProps<NavTarget, SpotlightFader.Props>> {
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
//        segment: Segment<SpotlightModel.State<NavTarget>>,
//        segmentProgress: Float
//    ): List<FrameModel<NavTarget>> {
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
//    override fun mapUpdate(update: Update<SpotlightModel.State<NavTarget>>): List<FrameModel<NavTarget>> {
//        TODO("Not yet implemented")
//    }
//}
