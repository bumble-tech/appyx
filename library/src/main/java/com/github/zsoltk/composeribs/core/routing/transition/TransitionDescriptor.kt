package com.github.zsoltk.composeribs.core.routing.transition

import com.github.zsoltk.composeribs.core.routing.Operation

data class TransitionDescriptor<T, S>(
    val transitionParams: TransitionParams,
    val operation: Operation<T, S>,
    val element: T,
    val fromState: S,
    val toState: S
)
