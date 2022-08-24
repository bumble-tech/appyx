package com.bumble.appyx.core.navigation.transition

import androidx.compose.runtime.Immutable
import com.bumble.appyx.core.navigation.Operation

@Immutable
data class TransitionDescriptor<T, out S>(
    val params: TransitionParams,
    val operation: Operation<T, out S>,
    val element: T,
    val fromState: S,
    val toState: S
)
