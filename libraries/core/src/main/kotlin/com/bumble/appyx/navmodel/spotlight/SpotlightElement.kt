package com.bumble.appyx.navmodel.spotlight

import com.bumble.appyx.core.navigation.NavElement
import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.navmodel.spotlight.Spotlight.State

typealias SpotlightElement<T> = NavElement<T, State>

typealias SpotlightElements<T> = NavElements<T, State>
