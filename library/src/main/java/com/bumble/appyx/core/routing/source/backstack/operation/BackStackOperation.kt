package com.bumble.appyx.core.routing.source.backstack.operation

import com.bumble.appyx.core.routing.Operation
import com.bumble.appyx.core.routing.source.backstack.BackStack

sealed interface BackStackOperation<T> : Operation<T, BackStack.TransitionState>
