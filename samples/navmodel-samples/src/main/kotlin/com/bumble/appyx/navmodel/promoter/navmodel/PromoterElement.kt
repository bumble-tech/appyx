package com.bumble.appyx.navmodel.promoter.navmodel

import com.bumble.appyx.navmodel.promoter.navmodel.Promoter.State
import com.bumble.appyx.core.navigation.NavElement
import com.bumble.appyx.core.navigation.NavElements

typealias PromoterElement<T> = NavElement<T, State>

typealias PromoterElements<T> = NavElements<T, State>
