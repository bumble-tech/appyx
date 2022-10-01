package com.bumble.appyx.navmodel2.backstack.operation

import com.bumble.appyx.core.navigation.Operation
import com.bumble.appyx.navmodel2.backstack.BackStack

interface BackStackOperation<T> : Operation<T, BackStack.State>
