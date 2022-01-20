package com.bumble.appyx.v2.core.routing.source.spotlight

import com.bumble.appyx.v2.core.routing.RoutingElement
import com.bumble.appyx.v2.core.routing.RoutingElements

typealias SpotlightElement<T> = RoutingElement<T, Spotlight.TransitionState>

typealias SpotlightElements<T> = RoutingElements<T, Spotlight.TransitionState>
