package com.bumble.appyx.navmodel.promoter.navmodel

import com.bumble.appyx.navmodel.promoter.navmodel.Promoter.TransitionState
import com.bumble.appyx.core.navigation.NavElement
import com.bumble.appyx.core.navigation.NavElements

typealias PromoterElement<T> = NavElement<T, TransitionState>

typealias PromoterElements<T> = NavElements<T, TransitionState>
