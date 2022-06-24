package com.bumble.appyx.routingsource.backstack.operation

import com.bumble.appyx.core.routing.Operation
import com.bumble.appyx.core.routing.RoutingKey
import com.bumble.appyx.routingsource.backstack.BackStack
import com.bumble.appyx.routingsource.backstack.BackStackElement
import com.bumble.appyx.routingsource.backstack.BackStackElements
import org.junit.Assert.assertEquals

internal sealed class Routing {
    object Routing1 : Routing()
    object Routing2 : Routing()
    object Routing3 : Routing()
    data class Routing4(val dummy: String) : Routing()
}

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
