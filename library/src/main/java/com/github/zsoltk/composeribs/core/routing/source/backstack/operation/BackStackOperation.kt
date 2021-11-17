package com.github.zsoltk.composeribs.core.routing.source.backstack.operation

import com.github.zsoltk.composeribs.core.routing.Operation
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack

sealed interface BackStackOperation<T> : Operation<T, BackStack.TransitionState>
