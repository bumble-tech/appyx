package com.bumble.appyx.navmodel.spotlightadvanced

import com.bumble.appyx.core.navigation.RoutingElement
import com.bumble.appyx.core.navigation.RoutingElements
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.TransitionState

typealias SpotlightAdvancedElement<T> = RoutingElement<T, TransitionState>

typealias SpotlightAdvancedElements<T> = RoutingElements<T, TransitionState>
