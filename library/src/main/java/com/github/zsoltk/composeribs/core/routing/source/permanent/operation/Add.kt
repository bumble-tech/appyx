package com.github.zsoltk.composeribs.core.routing.source.permanent.operation

import com.github.zsoltk.composeribs.core.routing.AlwaysOnScreen
import com.github.zsoltk.composeribs.core.routing.RoutingElement
import com.github.zsoltk.composeribs.core.routing.RoutingElements
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.source.permanent.PermanentOperation
import com.github.zsoltk.composeribs.core.routing.source.permanent.PermanentRoutingSource

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
                AlwaysOnScreen(),
                key = key,
                fromState = 0,
                targetState = 0,
                operation = this,
                isOnScreen = true
            )
        }
}

fun <T : Any> PermanentRoutingSource<T>.add(routingKey: RoutingKey<T>) {
    perform(Add(routingKey))
}
