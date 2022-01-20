package com.bumble.appyx.v2.core.routing.transition

import androidx.compose.runtime.Composable
import com.bumble.appyx.v2.core.composable.ChildTransitionScope

interface TransitionHandler<T, S> {

    @Composable
    fun handle(
        descriptor: TransitionDescriptor<T, S>,
        onTransitionFinished: (S) -> Unit
    ): ChildTransitionScope<S>
}
