package com.bumble.appyx.routingsource.spotlight

import com.bumble.appyx.core.routing.RoutingElement
import com.bumble.appyx.core.routing.RoutingElements
import com.bumble.appyx.routingsource.spotlight.Spotlight.TransitionState

typealias SpotlightElement<T> = RoutingElement<T, TransitionState>

typealias SpotlightElements<T> = RoutingElements<T, TransitionState>
