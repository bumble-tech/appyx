package com.bumble.appyx.core.routing

val RoutingElement<*, *>.isTransitioning: Boolean
    get() = fromState != targetState
