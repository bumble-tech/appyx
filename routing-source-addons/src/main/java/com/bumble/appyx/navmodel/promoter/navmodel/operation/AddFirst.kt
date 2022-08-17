package com.bumble.appyx.navmodel.promoter.navmodel.operation

import com.bumble.appyx.navmodel.promoter.navmodel.Promoter
import com.bumble.appyx.navmodel.promoter.navmodel.Promoter.TransitionState.CREATED
import com.bumble.appyx.navmodel.promoter.navmodel.PromoterElement
import com.bumble.appyx.navmodel.promoter.navmodel.PromoterElements
import com.bumble.appyx.core.navigation.RoutingElements
import com.bumble.appyx.core.navigation.RoutingKey
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
            fromState = CREATED,
            targetState = CREATED,
            operation = this
        )

        return listOf(new) + elements
    }
}

fun <T : Any> Promoter<T>.addFirst(element: T) {
    accept(AddFirst(element))
}
