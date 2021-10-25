package com.github.zsoltk.composeribs.core.routing.source.backstack.operation

import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack
import com.github.zsoltk.composeribs.core.routing.source.backstack.Elements
import com.github.zsoltk.composeribs.core.routing.source.backstack.UuidGenerator

/**
 * Operation:
 *
 * [A, B, C] + Remove(id of B) = [A, C]
 */
internal class Remove<T : Any>(
    private val key: RoutingKey<T>
) : BackStack.Operation<T> {

    override fun isApplicable(elements: Elements<T>) =
        elements.hasContentWithKey()

    override fun invoke(
        elements: Elements<T>,
        uuidGenerator: UuidGenerator
    ): Elements<T> =
        when {
            elements.hasContentWithKey() -> removeContent(elements)
            else -> elements
        }

    private fun Elements<T>.hasContentWithKey() =
        find { it.key == key } != null

    private fun removeContent(elements: Elements<T>): Elements<T> {
        val toRemove = elements.find { it.key == key }

        requireNotNull(toRemove)
        return elements.minus(toRemove)
    }
}

fun <T : Any> BackStack<T>.remove(key: RoutingKey<T>) {
    perform(Remove(key))
}
