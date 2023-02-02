package com.bumble.appyx.transitionmodel.backstack.interpolator

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.bumble.appyx.interactions.core.Segment
import com.bumble.appyx.interactions.core.Update
import com.bumble.appyx.interactions.core.ui.BaseProps
import com.bumble.appyx.interactions.core.ui.FrameModel
import com.bumble.appyx.interactions.core.ui.Interpolator
import com.bumble.appyx.interactions.core.ui.Interpolator.Companion.lerpFloat
import com.bumble.appyx.interactions.core.ui.MatchedProps
import com.bumble.appyx.transitionmodel.backstack.BackStackModel

class BackStackCrossfader<NavTarget : Any>() :
    Interpolator<NavTarget, BackStackModel.State<NavTarget>> {

    class Props(
        val alpha: Float,
        override val isVisible: Boolean
    ) : BaseProps

    private val visible = Props(
        alpha = 1f,
        isVisible = true
    )

    private val hidden = Props(
        alpha = 0f,
        isVisible = false
    )

    private fun <NavTarget : Any> BackStackModel.State<NavTarget>.toProps(): List<MatchedProps<NavTarget, Props>> =
        listOf(
            MatchedProps(active, visible)
        ) + (created + stashed + destroyed).map {
            MatchedProps(it, hidden)
        }

    override fun mapSegment(
        segment: Segment<BackStackModel.State<NavTarget>>,
        segmentProgress: Float
    ): List<FrameModel<NavTarget>> {
        val (fromState, targetState) = segment.navTransition
        val fromProps = fromState.toProps()
        val targetProps = targetState.toProps()

        return targetProps.map { t1 ->
            val t0 = fromProps.find { it.element.id == t1.element.id }!!
            val alpha = lerpFloat(t0.props.alpha, t1.props.alpha, segmentProgress)

            FrameModel(
                navElement = t1.element,
                modifier = Modifier
                    .alpha(alpha),
                progress = segmentProgress,
                state = resolveNavElementVisibility(
                    fromProps = t0.props,
                    toProps = t1.props,
                    progress = segmentProgress
                )
            )
        }
    }

    override fun mapUpdate(update: Update<BackStackModel.State<NavTarget>>): List<FrameModel<NavTarget>> {
        TODO("Not yet implemented")
    }
}
