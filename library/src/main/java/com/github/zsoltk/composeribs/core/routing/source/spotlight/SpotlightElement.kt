package com.github.zsoltk.composeribs.core.routing.source.spotlight

import com.github.zsoltk.composeribs.core.routing.RoutingElement
import com.github.zsoltk.composeribs.core.routing.RoutingElements

typealias SpotlightElement<T> = RoutingElement<T, Spotlight.TransitionState>

typealias SpotlightElements<T> = RoutingElements<T, Spotlight.TransitionState>
