package com.github.zsoltk.composeribs.core.routing.impl.backstack

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateOffset
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp

@Suppress("TransitionPropertiesLabel")
class BackStackSlider(
    private val transitionSpec: BackStackTransitionSpec = { tween(1500) }
) : BackStackTransitionHandler() {

    @Composable
    override fun map(transition: Transition<BackStack.TransitionState>): Modifier {
        val width = LocalConfiguration.current.screenWidthDp
        val offset = transition.animateOffset(
            transitionSpec = transitionSpec,
            targetValueByState = {
                when (it) {
                    BackStack.TransitionState.CREATED -> Offset(1.0f * width, 0f)
                    BackStack.TransitionState.ON_SCREEN -> Offset(0f, 0f)
                    BackStack.TransitionState.STASHED_IN_BACK_STACK -> Offset(-1.0f * width, 0f)
                    BackStack.TransitionState.DESTROYED -> Offset(1.0f * width, 0f)
                }
            })

        return Modifier
            .offset(Dp(offset.value.x), Dp(offset.value.y))
    }
}
