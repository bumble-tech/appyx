package com.bumble.appyx.v2.core.routing.source.backstack.operation

import com.bumble.appyx.v2.core.routing.Operation
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack

sealed interface BackStackOperation<T> : Operation<T, BackStack.TransitionState>
