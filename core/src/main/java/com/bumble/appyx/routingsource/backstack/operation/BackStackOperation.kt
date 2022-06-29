package com.bumble.appyx.routingsource.backstack.operation

import com.bumble.appyx.core.routing.Operation
import com.bumble.appyx.routingsource.backstack.BackStack

sealed interface BackStackOperation<T> : Operation<T, BackStack.TransitionState>
