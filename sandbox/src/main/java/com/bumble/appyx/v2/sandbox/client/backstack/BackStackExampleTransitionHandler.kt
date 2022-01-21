package com.bumble.appyx.v2.sandbox.client.backstack

import androidx.compose.animation.core.Transition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack
import com.bumble.appyx.v2.core.routing.source.backstack.BackStackFader
import com.bumble.appyx.v2.core.routing.source.backstack.BackStackSlider
import com.bumble.appyx.v2.core.routing.source.backstack.operation.Replace
import com.bumble.appyx.v2.core.routing.transition.ModifierTransitionHandler
import com.bumble.appyx.v2.core.routing.transition.TransitionDescriptor

class BackStackExampleTransitionHandler<T> :
    ModifierTransitionHandler<T, BackStack.TransitionState>(clipToBounds = true) {

    private val slider = BackStackSlider<T>(clipToBounds = clipToBounds)
    private val fader = BackStackFader<T>()

    override fun createModifier(
        modifier: Modifier,
        transition: Transition<BackStack.TransitionState>,
        descriptor: TransitionDescriptor<T, BackStack.TransitionState>
    ): Modifier =
        when (descriptor.operation) {
            is Replace -> fader.createModifier(modifier, transition, descriptor)
            else -> slider.createModifier(modifier, transition, descriptor)
        }
}

@Composable
fun <T> rememberBackStackExampleTransitionHandler(
): ModifierTransitionHandler<T, BackStack.TransitionState> = remember {
    BackStackExampleTransitionHandler()
}
