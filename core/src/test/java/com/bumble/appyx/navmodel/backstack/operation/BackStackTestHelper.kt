package com.bumble.appyx.navmodel.backstack.operation

import com.bumble.appyx.core.navigation.Operation
import com.bumble.appyx.core.navigation.RoutingKey
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.BackStackElement

internal fun <T : Routing> backStackElement(
    element: T,
    key: RoutingKey<T> = RoutingKey(routing = element),
    fromState: BackStack.TransitionState,
    targetState: BackStack.TransitionState,
    operation: Operation<T, BackStack.TransitionState>
) = BackStackElement(
    key = key,
    fromState = fromState,
    targetState = targetState,
    operation = operation
)
