package com.bumble.appyx.routingsource.spotlight

import com.bumble.appyx.core.routing.Operation
import com.bumble.appyx.core.routing.RoutingKey
import com.bumble.appyx.routingsource.spotlight.operation.Routing

internal fun <T : Routing> spotlightElement(
    element: T,
    key: RoutingKey<T> = RoutingKey(routing = element),
    fromState: Spotlight.TransitionState,
    targetState: Spotlight.TransitionState,
    operation: Operation<T, Spotlight.TransitionState> = Operation.Noop()
) = SpotlightElement(
    key = key,
    fromState = fromState,
    targetState = targetState,
    operation = operation
)
