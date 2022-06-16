package com.bumble.appyx.app.node.teaser.promoter.routingsource.operation

import com.bumble.appyx.app.node.teaser.promoter.routingsource.Promoter
import com.bumble.appyx.app.node.teaser.promoter.routingsource.PromoterElements
import com.bumble.appyx.core.routing.RoutingElements
import kotlinx.parcelize.Parcelize

@Parcelize
class PromoteAll<T : Any> : PromoterOperation<T> {

    override fun isApplicable(elements: PromoterElements<T>): Boolean =
        true

    override fun invoke(
        elements: PromoterElements<T>,
    ): RoutingElements<T, Promoter.TransitionState> =
        elements.map {
            it.transitionTo(
                targetState = it.targetState.next(),
                operation = this
            )
        }
}

fun <T : Any> Promoter<T>.promoteAll() {
    accept(PromoteAll())
}
