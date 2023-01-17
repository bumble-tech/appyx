package com.bumble.appyx.transitionmodel.backstack.operation

import com.bumble.appyx.interactions.core.Operation
import com.bumble.appyx.transitionmodel.backstack.BackStack

interface BackStackOperation<T> : Operation<T, BackStack.State>
