package com.bumble.appyx.routingsource.backstack

import com.bumble.appyx.core.routing.RoutingElement
import com.bumble.appyx.routingsource.backstack.BackStack.TransitionState

typealias BackStackElement<T> = RoutingElement<T, TransitionState>
