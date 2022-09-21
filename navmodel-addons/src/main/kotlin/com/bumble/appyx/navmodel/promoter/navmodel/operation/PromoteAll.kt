package com.bumble.appyx.navmodel.promoter.navmodel.operation

import com.bumble.appyx.navmodel.promoter.navmodel.Promoter
import com.bumble.appyx.navmodel.promoter.navmodel.PromoterElements
import com.bumble.appyx.core.navigation.NavElements
import kotlinx.parcelize.Parcelize

@Parcelize
class PromoteAll<T : Any> : PromoterOperation<T> {

    override fun isApplicable(elements: PromoterElements<T>): Boolean =
        true

    override fun invoke(
        elements: PromoterElements<T>,
    ): NavElements<T, Promoter.TransitionState> =
        elements.transitionTo { it.targetState.next() }
}

fun <T : Any> Promoter<T>.promoteAll() {
    accept(PromoteAll())
}
