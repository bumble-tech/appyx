package com.bumble.appyx.transitionmodel.promoter

import com.bumble.appyx.interactions.core.NavElement
import com.bumble.appyx.interactions.core.NavElements
import com.bumble.appyx.transitionmodel.promoter.PromoterModel.State

typealias PromoterElement<NavTarget> = NavElement<NavTarget, State>

typealias PromoterElements<NavTarget> = NavElements<NavTarget, State>
