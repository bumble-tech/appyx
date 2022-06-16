package com.bumble.appyx.core.routing.source.permanent.operation

import com.bumble.appyx.core.routing.RoutingElement
import com.bumble.appyx.core.routing.RoutingElements
import com.bumble.appyx.core.routing.RoutingKey
import com.bumble.appyx.core.routing.source.permanent.PermanentRoutingSource
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

fun <T : Any> PermanentRoutingSource<T>.add(routingKey: RoutingKey<T>) {
    accept(Add(routingKey))
}
