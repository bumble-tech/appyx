package com.github.zsoltk.composeribs.core.routing.transition

import androidx.compose.animation.core.Transition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class CombinedHandler<T, S>(
    private val handlers: List<UpdateTransitionHandler<T, S>>
) : UpdateTransitionHandler<T, S>() {

    @Composable
    override fun map(transition: Transition<S>, descriptor: TransitionDescriptor<T, S>): Modifier =
        handlers
            .map { it.map(transition, descriptor = descriptor) }
            .fold(Modifier) { acc: Modifier, modifier: Modifier ->
                acc.then(modifier)
            }
}
