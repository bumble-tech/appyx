package com.bumble.appyx.transitionmodel.spotlight.interpolator

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.bumble.appyx.interactions.core.TransitionModel
import com.bumble.appyx.interactions.core.ui.FrameModel
import com.bumble.appyx.interactions.core.ui.TransitionParams
import com.bumble.appyx.interactions.core.ui.Interpolator
import com.bumble.appyx.interactions.core.ui.Interpolator.Companion.lerpFloat
import com.bumble.appyx.interactions.core.ui.MatchedProps
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel

class SpotlightFader<NavTarget : Any>(
    transitionParams: TransitionParams
) : Interpolator<NavTarget, SpotlightModel.State<NavTarget>> {

    class Props(
        val alpha: Float
    )

    private val visible = Props(
        alpha = 1f
    )

    private val hidden = Props(
        alpha = 0f
    )

    private fun <NavTarget : Any> SpotlightModel.State<NavTarget>.toProps(): List<MatchedProps<NavTarget, Props>> =
        standard.map {
            MatchedProps(it, visible)
        } + (created + destroyed).map {
            MatchedProps(it, hidden)
        }

    override fun map(segment: TransitionModel.Segment<SpotlightModel.State<NavTarget>>): List<FrameModel<NavTarget>> {
        val (fromState, targetState) = segment.navTransition
        val fromProps = fromState.toProps()
        val targetProps = targetState.toProps()

        return targetProps.map { t1 ->
            val t0 = fromProps.find { it.element.id == t1.element.id }!!
            val alpha = lerpFloat(t0.props.alpha, t1.props.alpha, segment.progress)

            FrameModel(
                navElement = t1.element,
                modifier = Modifier
                    .alpha(alpha),
                progress = segment.progress
            )
        }
    }
}
