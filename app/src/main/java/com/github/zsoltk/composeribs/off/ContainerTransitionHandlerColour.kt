package com.github.zsoltk.composeribs.off

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack
import com.github.zsoltk.composeribs.core.routing.transition.ModifierTransitionHandler
import com.github.zsoltk.composeribs.core.routing.transition.TransitionDescriptor

@Suppress("TransitionPropertiesLabel")
object ContainerTransitionHandlerColour :
    ModifierTransitionHandler<Any, BackStack.TransitionState>() {

    override fun createModifier(
        modifier: Modifier,
        transition: Transition<BackStack.TransitionState>,
        descriptor: TransitionDescriptor<Any, BackStack.TransitionState>
    ): Modifier = modifier.composed {
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

        background(color = color.value)
    }

}
