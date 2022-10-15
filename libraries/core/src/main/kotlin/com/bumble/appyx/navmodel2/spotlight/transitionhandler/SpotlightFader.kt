package com.bumble.appyx.navmodel2.spotlight.transitionhandler

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.bumble.appyx.core.navigation.transition.TransitionParams
import com.bumble.appyx.core.navigation2.NavModel.State
import com.bumble.appyx.core.navigation2.ui.Modifiers
import com.bumble.appyx.core.navigation2.ui.UiProps
import com.bumble.appyx.core.navigation2.ui.UiProps.Companion.lerp
import com.bumble.appyx.navmodel2.spotlight.Spotlight

class SpotlightFader<MavTarget>(
    transitionParams: TransitionParams
) : UiProps<MavTarget, Spotlight.State> {
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

    override fun map(segment: State<MavTarget, Spotlight.State>): List<Modifiers<MavTarget, Spotlight.State>> {
        val (fromState, targetState) = segment.navTransition

        return targetState.map { t1 ->
            val t0 = fromState.find { it.key == t1.key }!!

            val fromProps = t0.state.toProps()
            val targetProps = t1.state.toProps()
            val alpha = lerp(fromProps.alpha, targetProps.alpha, segment.progress)

            Modifiers(
                navElement = t1,
                modifier = Modifier
                    .alpha(alpha)
            )
        }
    }
}
