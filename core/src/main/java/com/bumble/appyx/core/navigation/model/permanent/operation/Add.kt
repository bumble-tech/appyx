package com.bumble.appyx.core.navigation.model.permanent.operation

import com.bumble.appyx.core.navigation.RoutingElement
import com.bumble.appyx.core.navigation.RoutingElements
import com.bumble.appyx.core.navigation.RoutingKey
import com.bumble.appyx.core.navigation.model.permanent.PermanentNavModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Add<T : Any>(
    private val key: RoutingKey<T>
) : PermanentOperation<T> {

    override fun isApplicable(elements: RoutingElements<T, Int>): Boolean =
        !elements.any { it.key == key }

    override fun invoke(
        elements: RoutingElements<T, Int>
    ): RoutingElements<T, Int> =
        if (elements.any { it.key == key }) {
            elements
        } else {
            elements + RoutingElement(
                key = key,
                fromState = 0,
                targetState = 0,
                operation = this,
            )
        }
}

fun <T : Any> PermanentNavModel<T>.add(routingKey: RoutingKey<T>) {
    accept(Add(routingKey))
}
