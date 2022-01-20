package com.bumble.appyx.v2.core.routing.source.backstack

import androidx.compose.animation.core.Transition
import androidx.compose.ui.Modifier
import com.bumble.appyx.v2.core.routing.transition.ModifierTransitionHandler
import com.bumble.appyx.v2.core.routing.transition.TransitionDescriptor

@Suppress("TransitionPropertiesLabel")
class JumpToEndTransitionHandler<T, S> : ModifierTransitionHandler<T, S>() {

    override fun createModifier(
        modifier: Modifier,
        transition: Transition<S>,
        descriptor: TransitionDescriptor<T, S>
    ): Modifier = modifier
}
