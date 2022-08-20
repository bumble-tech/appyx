package com.bumble.appyx.navmodel.spotlight

import com.bumble.appyx.core.navigation.RoutingElement
import com.bumble.appyx.core.navigation.RoutingElements
import com.bumble.appyx.navmodel.spotlight.Spotlight.TransitionState

typealias SpotlightElement<T> = RoutingElement<T, TransitionState>

typealias SpotlightElements<T> = RoutingElements<T, TransitionState>
