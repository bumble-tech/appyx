package com.github.zsoltk.composeribs.core.routing.transition

import androidx.compose.runtime.Composable
import com.github.zsoltk.composeribs.core.ChildTransitionScope
import com.github.zsoltk.composeribs.core.TransitionParams

interface TransitionHandler<S> {

    @Composable
    fun handle(
        params: TransitionParams,
        fromState: S,
        toState: S,
        onTransitionFinished: (S) -> Unit
    ): ChildTransitionScope<S>
}
