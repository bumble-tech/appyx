package com.github.zsoltk.composeribs.core.routing.transition

import androidx.compose.animation.core.Transition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

internal class CombinedHandler<S>(
    private val handlers: List<UpdateTransitionHandler<S>>
) : UpdateTransitionHandler<S>() {

    @Composable
    override fun map(transition: Transition<S>, transitionBounds: TransitionBounds): Modifier =
        handlers
            .map { it.map(transition, transitionBounds = transitionBounds) }
            .fold(Modifier) { acc: Modifier, modifier: Modifier ->
                acc.then(modifier)
            }

}

@Composable
fun <S> rememberCombinedHandler(handlers: List<UpdateTransitionHandler<S>>): UpdateTransitionHandler<S> =
    remember { CombinedHandler(handlers = handlers) }
