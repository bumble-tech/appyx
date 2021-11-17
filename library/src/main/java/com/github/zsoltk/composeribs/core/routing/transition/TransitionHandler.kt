package com.github.zsoltk.composeribs.core.routing.transition

import androidx.compose.runtime.Composable
import com.github.zsoltk.composeribs.core.composable.ChildTransitionScope

interface TransitionHandler<S> {

    @Composable
    fun handle(
        transitionParams: TransitionParams,
        fromState: S,
        toState: S,
        onTransitionFinished: (S) -> Unit
    ): ChildTransitionScope<S>
}
