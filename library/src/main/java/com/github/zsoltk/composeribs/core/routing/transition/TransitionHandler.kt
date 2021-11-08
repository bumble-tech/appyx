package com.github.zsoltk.composeribs.core.routing.transition

import androidx.compose.runtime.Composable
import com.github.zsoltk.composeribs.core.ChildTransitionScope

interface TransitionHandler<T, S> {

    @Composable
    fun handle(
        descriptor: TransitionDescriptor<T, S>,
        onTransitionFinished: (S) -> Unit
    ): ChildTransitionScope<S>
}
