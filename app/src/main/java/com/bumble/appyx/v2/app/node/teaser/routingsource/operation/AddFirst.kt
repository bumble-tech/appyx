package com.bumble.appyx.v2.app.node.teaser.routingsource.operation

import com.bumble.appyx.v2.app.node.teaser.routingsource.Promoter
import com.bumble.appyx.v2.app.node.teaser.routingsource.PromoterElement
import com.bumble.appyx.v2.app.node.teaser.routingsource.PromoterElements
import com.bumble.appyx.v2.core.routing.RoutingElements
import com.bumble.appyx.v2.core.routing.RoutingKey
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class AddFirst<T : Any>(
    private val element: @RawValue T
) : PromoterOperation<T> {

    override fun isApplicable(elements: PromoterElements<T>): Boolean =
        true

    override fun invoke(
        elements: PromoterElements<T>,
    ): RoutingElements<T, Promoter.TransitionState> {
        val new = PromoterElement(
            key = RoutingKey(element),
            fromState = Promoter.TransitionState.CREATED,
            targetState = Promoter.TransitionState.CREATED,
            operation = this
        )

        return listOf(new) + elements
    }
}

fun <T : Any> Promoter<T>.addFirst(element: T) {
    accept(AddFirst(element))
}
