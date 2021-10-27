package com.github.zsoltk.composeribs.core.routing.transition

import androidx.compose.animation.core.Transition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize

class CombinedHandler<S>(
    private val handlers: List<UpdateTransitionHandler<S>>
) : UpdateTransitionHandler<S>() {

    @Composable
    override fun map(transition: Transition<S>, transitionBoundsDp: IntSize): Modifier =
        handlers
            .map { it.map(transition, transitionBoundsDp = transitionBoundsDp) }
            .fold(Modifier) { acc: Modifier, modifier: Modifier ->
                acc.then(modifier)
            }

}
