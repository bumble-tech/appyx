package com.github.zsoltk.composeribs.core.routing.transition

import androidx.compose.runtime.Composable
import com.github.zsoltk.composeribs.core.ChildTransitionScope

interface TransitionHandler<S> {

    @Composable
    fun handle(fromState: S, toState: S, onTransitionFinished: (S) -> Unit): ChildTransitionScope<S>
}
