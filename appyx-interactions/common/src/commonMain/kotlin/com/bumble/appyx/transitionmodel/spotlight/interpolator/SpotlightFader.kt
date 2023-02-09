package com.bumble.appyx.transitionmodel.spotlight.interpolator

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import com.bumble.appyx.interactions.core.Segment
import com.bumble.appyx.interactions.core.Update
import com.bumble.appyx.interactions.core.ui.BaseProps
import com.bumble.appyx.interactions.core.ui.FrameModel
import com.bumble.appyx.interactions.core.ui.Interpolator
import com.bumble.appyx.interactions.core.ui.Interpolator.Companion.lerpFloat
import com.bumble.appyx.interactions.core.ui.MatchedProps
import com.bumble.appyx.interactions.core.ui.TransitionParams
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel.State.ElementState.CREATED
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel.State.ElementState.DESTROYED
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel.State.ElementState.STANDARD


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
