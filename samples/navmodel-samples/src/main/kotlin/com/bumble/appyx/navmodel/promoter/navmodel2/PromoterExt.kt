package com.bumble.appyx.navmodel.promoter.navmodel2

import com.bumble.appyx.navmodel.promoter.navmodel2.Promoter.State
import com.bumble.appyx.core.navigation2.NavElement
import com.bumble.appyx.core.navigation2.NavElements

typealias PromoterElement<NavTarget> = NavElement<NavTarget, State>

typealias PromoterElements<NavTarget> = NavElements<NavTarget, State>
