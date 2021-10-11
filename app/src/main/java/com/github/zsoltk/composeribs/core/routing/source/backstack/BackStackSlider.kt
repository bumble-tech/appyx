package com.github.zsoltk.composeribs.core.routing.source.backstack

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateOffset
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import com.github.zsoltk.composeribs.core.routing.transition.TransitionSpec
import com.github.zsoltk.composeribs.core.routing.transition.UpdateTransitionHandler

@Suppress("TransitionPropertiesLabel")
class BackStackSlider(
    private val transitionSpec: TransitionSpec<BackStack.TransitionState, Offset> = { tween(1500) }
) : UpdateTransitionHandler<BackStack.TransitionState>() {

    @Composable
    override fun map(transition: Transition<BackStack.TransitionState>): Modifier {
        val width = LocalConfiguration.current.screenWidthDp
        val offset = transition.animateOffset(
            transitionSpec = transitionSpec,
            targetValueByState = {
                when (it) {
                    BackStack.TransitionState.Created -> Offset(1.0f * width, 0f)
                    BackStack.TransitionState.OnScreen -> Offset(0f, 0f)
                    BackStack.TransitionState.StashedInBackstack -> Offset(-1.0f * width, 0f)
                    BackStack.TransitionState.Destroyed -> Offset(1.0f * width, 0f)
                }
            })

        return Modifier
            .offset(Dp(offset.value.x), Dp(offset.value.y))
    }
}
