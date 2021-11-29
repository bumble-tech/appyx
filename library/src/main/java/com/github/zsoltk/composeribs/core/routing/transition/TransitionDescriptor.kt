package com.github.zsoltk.composeribs.core.routing.transition

import com.github.zsoltk.composeribs.core.routing.Operation

data class TransitionDescriptor<T, out S>(
    val uid: String,
    val params: TransitionParams,
    val operation: Operation<T, out S>,
    val element: T,
    val fromState: S,
    val toState: S
)
