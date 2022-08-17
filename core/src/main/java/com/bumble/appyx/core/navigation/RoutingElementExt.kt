package com.bumble.appyx.core.navigation

val RoutingElement<*, *>.isTransitioning: Boolean
    get() = fromState != targetState
