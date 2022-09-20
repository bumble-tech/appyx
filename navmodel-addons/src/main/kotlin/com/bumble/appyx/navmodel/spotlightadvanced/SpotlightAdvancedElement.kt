package com.bumble.appyx.navmodel.spotlightadvanced

import com.bumble.appyx.core.navigation.NavElement
import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.TransitionState

typealias SpotlightAdvancedElement<T> = NavElement<T, TransitionState>

typealias SpotlightAdvancedElements<T> = NavElements<T, TransitionState>
