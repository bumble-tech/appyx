package com.bumble.appyx.navmodel.backstack

import com.bumble.appyx.core.navigation.RoutingElement
import com.bumble.appyx.navmodel.backstack.BackStack.TransitionState

typealias BackStackElement<T> = RoutingElement<T, TransitionState>
