package com.github.zsoltk.composeribs.core.routing.source.backstack.operation

import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackElement
import com.github.zsoltk.composeribs.core.routing.source.backstack.UuidGenerator

/**
 * Operation:
 *
 * [A, B, C] + Remove(id of B) = [A, C]
 */
internal class Remove<T : Any>(
    private val key: RoutingKey<T>
) : BackStack.Operation<T> {

    override fun isApplicable(elements: List<BackStackElement<T>>) =
        elements.hasContentWithKey()

    override fun invoke(
        elements: List<BackStackElement<T>>,
        uuidGenerator: UuidGenerator
    ): List<BackStackElement<T>> =
        when {
            elements.hasContentWithKey() -> removeContent(elements)
            else -> elements
        }

    private fun List<BackStackElement<T>>.hasContentWithKey() =
        find { it.key == key } != null

    private fun removeContent(elements: List<BackStackElement<T>>): List<BackStackElement<T>> {
        val toRemove = elements.find { it.key == key }

        requireNotNull(toRemove)
        return elements.minus(toRemove)
    }
}

fun <T : Any> BackStack<T>.remove(key: RoutingKey<T>) {
    perform(Remove(key))
}
