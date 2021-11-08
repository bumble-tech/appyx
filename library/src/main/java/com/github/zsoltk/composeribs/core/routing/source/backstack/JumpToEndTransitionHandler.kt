package com.github.zsoltk.composeribs.core.routing.source.backstack

import androidx.compose.animation.core.Transition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.zsoltk.composeribs.core.routing.transition.TransitionDescriptor
import com.github.zsoltk.composeribs.core.routing.transition.UpdateTransitionHandler

@Suppress("TransitionPropertiesLabel")
class JumpToEndTransitionHandler<T, S> : UpdateTransitionHandler<T, S>() {

    @Composable
    override fun map(transition: Transition<S>, descriptor: TransitionDescriptor<T, S>): Modifier =
        Modifier
}
