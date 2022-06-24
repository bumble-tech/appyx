package com.bumble.appyx.routingsource.spotlight

import com.bumble.appyx.core.routing.Operation
import com.bumble.appyx.core.routing.RoutingKey

internal sealed class Routing {
    object Routing1 : Routing()
    object Routing2 : Routing()
    object Routing3 : Routing()
    data class Routing4(val dummy: String) : Routing()
}

internal fun <T : Routing> spotLightElement(
    element: T,
    key: RoutingKey<T> = RoutingKey(routing = element),
    fromState: Spotlight.TransitionState,
    targetState: Spotlight.TransitionState,
    operation: Operation<T, Spotlight.TransitionState>
) = SpotlightElement(
    key = key,
    fromState = fromState,
    targetState = targetState,
    operation = operation
)

