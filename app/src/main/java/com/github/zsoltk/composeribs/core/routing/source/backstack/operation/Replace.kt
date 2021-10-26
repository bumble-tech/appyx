package com.github.zsoltk.composeribs.core.routing.source.backstack.operation

import com.github.zsoltk.composeribs.core.routing.source.backstack.*

/**
 * Operation:
 *
 * [A, B, C] + Replace(D) = [A, B, D]
 */
internal class Replace<T : Any>(
    private val element: T
) : BackStack.Operation<T> {

    override fun isApplicable(elements: Elements<T>): Boolean =
        element != elements.current?.key?.routing

    override fun invoke(
        elements: Elements<T>,
        uuidGenerator: UuidGenerator
    ): Elements<T> {
        require(elements.isNotEmpty()) { "No element to be replaced, state=$elements" }

        return elements.apply {
            last().copy(targetState = BackStack.TransitionState.DESTROYED)
        } + BackStackElement(
            key = BackStack.LocalRoutingKey(element, uuidGenerator.incrementAndGet()),
            fromState = BackStack.TransitionState.CREATED,
            targetState = BackStack.TransitionState.ON_SCREEN,
        )
    }
}

fun <T : Any> BackStack<T>.replace(element: T) {
    perform(Replace(element))
}
