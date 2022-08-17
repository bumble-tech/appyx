package com.bumble.appyx.navmodel.promoter.navmodel

import com.bumble.appyx.navmodel.promoter.navmodel.Promoter.TransitionState
import com.bumble.appyx.core.navigation.RoutingElement
import com.bumble.appyx.core.navigation.RoutingElements

typealias PromoterElement<T> = RoutingElement<T, TransitionState>

typealias PromoterElements<T> = RoutingElements<T, TransitionState>
