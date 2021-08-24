package com.github.zsoltk.composeribs.core.routing.transition

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface TransitionHandler<S> {

    @Composable
    fun handle(fromState: S, toState: S, onTransitionFinished: (S) -> Unit): Modifier
}
