package com.bumble.appyx.v2.core.routing.source.spotlight

import com.bumble.appyx.v2.core.routing.Operation
import com.bumble.appyx.v2.core.routing.RoutingElements
import com.bumble.appyx.v2.core.routing.RoutingKey
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack
import com.bumble.appyx.v2.core.routing.source.backstack.BackStackElement
import com.bumble.appyx.v2.core.routing.source.backstack.BackStackElements
import org.junit.Assert.assertEquals

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

