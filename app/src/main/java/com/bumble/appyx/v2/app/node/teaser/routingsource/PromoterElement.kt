package com.bumble.appyx.v2.app.node.teaser.routingsource

import com.bumble.appyx.v2.core.routing.RoutingElement
import com.bumble.appyx.v2.core.routing.RoutingElements

typealias PromoterElement<T> = RoutingElement<T, Promoter.TransitionState>

typealias PromoterElements<T> = RoutingElements<T, Promoter.TransitionState>
