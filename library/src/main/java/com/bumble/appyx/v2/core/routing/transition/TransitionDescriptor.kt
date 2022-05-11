package com.bumble.appyx.v2.core.routing.transition

import androidx.compose.runtime.Immutable
import com.bumble.appyx.v2.core.routing.Operation

@Immutable
data class TransitionDescriptor<T, out S>(
    val params: TransitionParams,
    val operation: Operation<T, out S>,
    val element: T,
    val fromState: S,
    val toState: S
)
