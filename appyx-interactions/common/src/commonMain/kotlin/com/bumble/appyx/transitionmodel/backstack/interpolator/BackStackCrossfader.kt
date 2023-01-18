package com.bumble.appyx.transitionmodel.backstack.interpolator

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.bumble.appyx.interactions.core.TransitionModel
import com.bumble.appyx.interactions.core.ui.FrameModel
import com.bumble.appyx.interactions.core.ui.TransitionParams
import com.bumble.appyx.interactions.core.ui.Interpolator
import com.bumble.appyx.interactions.core.ui.Interpolator.Companion.lerpFloat
import com.bumble.appyx.transitionmodel.backstack.BackStack

class BackStackCrossfader<NavTarget>(
    transitionParams: TransitionParams
) : Interpolator<NavTarget, BackStack.State> {
    private val width = transitionParams.bounds.width

    class Props(
        val alpha: Float
    )

    private val visible = Props(
        alpha = 1f
    )

    private val hidden = Props(
        alpha = 0f
    )

    private fun BackStack.State.toProps(): Props =
        when (this) {
            BackStack.State.ACTIVE -> visible
            else -> hidden
        }

    override fun map(segment: TransitionModel.Segment<NavTarget, BackStack.State>): List<FrameModel<NavTarget, BackStack.State>> {
        val (fromState, targetState) = segment.navTransition

        return targetState.map { t1 ->
            val t0 = fromState.find { it.key == t1.key }!!

            val fromProps = t0.state.toProps()
            val targetProps = t1.state.toProps()
            val alpha = lerpFloat(fromProps.alpha, targetProps.alpha, segment.progress)

            FrameModel(
                navElement = t1,
                modifier = Modifier
                    .alpha(alpha),
                progress = segment.progress
            )
        }
    }
}
