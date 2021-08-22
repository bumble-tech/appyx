package com.github.zsoltk.composeribs.client.container

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.github.zsoltk.composeribs.core.BackStack
import com.github.zsoltk.composeribs.core.BackStackTransitionHandler

@Suppress("TransitionPropertiesLabel")
object ContainerTransitionHandler : BackStackTransitionHandler() {

    @Composable
    override fun map(transition: Transition<BackStack.TransitionState>): Modifier {
        val color = transition.animateColor(
            transitionSpec = { tween(3500) },
            targetValueByState = {
                when (it) {
                    BackStack.TransitionState.CREATED -> Color.Blue
                    BackStack.TransitionState.ON_SCREEN -> Color.Green
                    BackStack.TransitionState.STASHED_IN_BACK_STACK -> Color.LightGray
                    BackStack.TransitionState.DESTROYED -> Color.Red
                }
            })

        return Modifier.background(color = color.value)
    }
}
