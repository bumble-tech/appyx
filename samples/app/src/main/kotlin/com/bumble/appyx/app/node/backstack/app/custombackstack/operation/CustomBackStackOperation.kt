package com.bumble.appyx.app.node.backstack.app.custombackstack.operation

import com.bumble.appyx.app.node.backstack.app.custombackstack.CustomBackStack
import com.bumble.appyx.core.navigation.Operation

interface CustomBackStackOperation<T> : Operation<T, CustomBackStack.State>
