package com.bumble.appyx.core.routing.transition

import androidx.compose.animation.core.Transition
import androidx.compose.ui.Modifier

@Suppress("TransitionPropertiesLabel")
class JumpToEndTransitionHandler<T, S> : ModifierTransitionHandler<T, S>() {

    override fun createModifier(
        modifier: Modifier,
        transition: Transition<S>,
        descriptor: TransitionDescriptor<T, S>
    ): Modifier = modifier
}
