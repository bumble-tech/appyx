package com.bumble.appyx.navmodel.backstack.operation

import com.bumble.appyx.core.navigation.Operation
import com.bumble.appyx.navmodel.backstack.BackStack

interface BackStackOperation<T> : Operation<T, BackStack.TransitionState>
