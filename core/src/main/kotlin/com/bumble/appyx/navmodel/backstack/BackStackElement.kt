package com.bumble.appyx.navmodel.backstack

import com.bumble.appyx.core.navigation.NavElement
import com.bumble.appyx.navmodel.backstack.BackStack.TransitionState

typealias BackStackElement<T> = NavElement<T, TransitionState>
