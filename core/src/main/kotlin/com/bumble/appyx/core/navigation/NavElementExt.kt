package com.bumble.appyx.core.navigation

val NavElement<*, *>.isTransitioning: Boolean
    get() = fromState != targetState
