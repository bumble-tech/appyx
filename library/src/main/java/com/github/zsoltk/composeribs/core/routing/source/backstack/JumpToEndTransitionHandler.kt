package com.github.zsoltk.composeribs.core.routing.source.backstack

import androidx.compose.animation.core.Transition
import androidx.compose.ui.Modifier
import com.github.zsoltk.composeribs.core.routing.transition.TransitionBounds
import com.github.zsoltk.composeribs.core.routing.transition.UpdateTransitionHandler

@Suppress("TransitionPropertiesLabel")
class JumpToEndTransitionHandler<S> : UpdateTransitionHandler<S>() {
    override fun map(
        modifier: Modifier,
        transition: Transition<S>,
        transitionBounds: TransitionBounds
    ): Modifier = modifier
}
