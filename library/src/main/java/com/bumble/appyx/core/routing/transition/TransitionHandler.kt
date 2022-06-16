package com.bumble.appyx.core.routing.transition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.bumble.appyx.core.composable.ChildTransitionScope

@Stable
interface TransitionHandler<T, S> {

    @Composable
    fun handle(
        descriptor: TransitionDescriptor<T, S>,
        onTransitionFinished: (S) -> Unit
    ): ChildTransitionScope<S>
}
