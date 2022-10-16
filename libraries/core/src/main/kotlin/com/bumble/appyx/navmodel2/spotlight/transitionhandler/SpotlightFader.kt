package com.bumble.appyx.navmodel2.spotlight.transitionhandler

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.bumble.appyx.core.navigation.transition.TransitionParams
import com.bumble.appyx.core.navigation2.NavModel.Segment
import com.bumble.appyx.core.navigation2.ui.RenderParams
import com.bumble.appyx.core.navigation2.ui.UiProps
import com.bumble.appyx.core.navigation2.ui.UiProps.Companion.lerp
import com.bumble.appyx.navmodel2.spotlight.Spotlight

class SpotlightFader<NavTarget>(
    transitionParams: TransitionParams
) : UiProps<NavTarget, Spotlight.State> {
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

    private fun Spotlight.State.toProps(): Props =
        when (this) {
            Spotlight.State.ACTIVE -> visible
            else -> hidden
        }

    override fun map(segment: Segment<NavTarget, Spotlight.State>): List<RenderParams<NavTarget, Spotlight.State>> {
        val (fromState, targetState) = segment.navTransition

        return targetState.map { t1 ->
            val t0 = fromState.find { it.key == t1.key }!!

            val fromProps = t0.state.toProps()
            val targetProps = t1.state.toProps()
            val alpha = lerp(fromProps.alpha, targetProps.alpha, segment.progress)

            RenderParams(
                navElement = t1,
                modifier = Modifier
                    .alpha(alpha)
            )
        }
    }
}
