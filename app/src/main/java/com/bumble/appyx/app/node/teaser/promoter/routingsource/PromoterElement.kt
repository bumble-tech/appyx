package com.bumble.appyx.app.node.teaser.promoter.routingsource

import com.bumble.appyx.core.routing.RoutingElement
import com.bumble.appyx.core.routing.RoutingElements

typealias PromoterElement<T> = RoutingElement<T, Promoter.TransitionState>

typealias PromoterElements<T> = RoutingElements<T, Promoter.TransitionState>
