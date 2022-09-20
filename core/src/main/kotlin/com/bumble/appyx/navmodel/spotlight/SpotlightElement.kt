package com.bumble.appyx.navmodel.spotlight

import com.bumble.appyx.core.navigation.NavElement
import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.navmodel.spotlight.Spotlight.TransitionState

typealias SpotlightElement<T> = NavElement<T, TransitionState>

typealias SpotlightElements<T> = NavElements<T, TransitionState>
