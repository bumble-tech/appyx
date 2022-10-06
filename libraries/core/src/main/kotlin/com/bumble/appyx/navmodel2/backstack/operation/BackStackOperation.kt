package com.bumble.appyx.navmodel2.backstack.operation

import com.bumble.appyx.core.navigation2.Operation
import com.bumble.appyx.navmodel2.backstack.BackStack

interface BackStackOperation<T> : Operation<T, BackStack.State>
