package com.github.zsoltk.composeribs.core.routing.transition

import androidx.compose.animation.core.Transition
import androidx.compose.ui.Modifier

class CombinedHandler<S>(
    private val handlers: List<ModifierTransitionHandler<S>>
) : ModifierTransitionHandler<S>() {

    override fun createModifier(
        modifier: Modifier,
        transition: Transition<S>, transitionBounds: TransitionBounds
    ): Modifier =
        handlers
            .map { it.createModifier(Modifier, transition, transitionBounds = transitionBounds) }
            .fold(modifier) { acc: Modifier, modifier: Modifier ->
                acc.then(modifier)
            }

}
