package com.github.zsoltk.composeribs.core.routing.transition

import androidx.compose.animation.core.Transition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

class CombinedHandler<T, S>(
    private val handlers: List<ModifierTransitionHandler<T, S>>
) : ModifierTransitionHandler<T, S>() {

    override fun createModifier(
        modifier: Modifier,
        transition: Transition<S>,
        descriptor: TransitionDescriptor<T, S>
    ): Modifier =
        handlers
            .map { it.createModifier(Modifier, transition, descriptor = descriptor) }
            .fold(modifier) { acc: Modifier, modifier: Modifier ->
                acc.then(modifier)
            }
}

@Composable
fun <T, S> rememberCombinedHandler(handlers: List<ModifierTransitionHandler<T, S>>): ModifierTransitionHandler<T, S> =
    remember { CombinedHandler(handlers = handlers) }
