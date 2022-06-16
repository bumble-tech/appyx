package com.bumble.appyx.core.routing.transition

import androidx.compose.runtime.Immutable
import com.bumble.appyx.core.routing.Operation

@Immutable
data class TransitionDescriptor<T, out S>(
    val params: TransitionParams,
    val operation: Operation<T, out S>,
    val element: T,
    val fromState: S,
    val toState: S
)
