package com.bumble.appyx.v2.core.routing

val RoutingElement<*, *>.isTransitioning: Boolean
    get() = fromState != targetState
