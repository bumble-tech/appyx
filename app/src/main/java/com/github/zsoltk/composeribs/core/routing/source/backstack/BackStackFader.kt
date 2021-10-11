package com.github.zsoltk.composeribs.core.routing.source.backstack

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import com.github.zsoltk.composeribs.core.routing.transition.TransitionSpec
import com.github.zsoltk.composeribs.core.routing.transition.UpdateTransitionHandler

@Suppress("TransitionPropertiesLabel")
class BackStackFader(
    private val transitionSpec: TransitionSpec<BackStack.TransitionState, Float> = { tween(1500) }
) : UpdateTransitionHandler<BackStack.TransitionState>() {

    @Composable
    override fun map(transition: Transition<BackStack.TransitionState>): Modifier {
        val alpha = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = {
                when (it) {
                    BackStack.TransitionState.OnScreen -> 1f
                    else -> 0f
                }
            })

        return Modifier
            .alpha(alpha.value)
    }
}
