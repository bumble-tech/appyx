package com.github.zsoltk.composeribs.core.routing.source.backstack

import androidx.compose.animation.core.Transition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import com.github.zsoltk.composeribs.core.routing.transition.UpdateTransitionHandler

@Suppress("TransitionPropertiesLabel")
class JumpToEndTransitionHandler<S> : UpdateTransitionHandler<S>() {
    @Composable
    override fun map(transition: Transition<S>, transitionBoundsDp: IntSize): Modifier = Modifier
}
