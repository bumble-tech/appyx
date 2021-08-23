package com.github.zsoltk.composeribs.client.container

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateOffset
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import com.github.zsoltk.composeribs.core.routing.impl.backstack.BackStack
import com.github.zsoltk.composeribs.core.routing.impl.backstack.BackStackTransitionHandler

@Suppress("TransitionPropertiesLabel")
object ContainerTransitionHandler : BackStackTransitionHandler() {

    @Composable
    override fun map(transition: Transition<BackStack.TransitionState>): Modifier {
        val width = LocalConfiguration.current.screenWidthDp
        val offset = transition.animateOffset(
            transitionSpec = { tween(1500) },
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
