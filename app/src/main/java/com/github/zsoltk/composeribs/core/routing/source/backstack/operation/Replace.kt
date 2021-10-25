package com.github.zsoltk.composeribs.core.routing.source.backstack.operation

import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackElement
import com.github.zsoltk.composeribs.core.routing.source.backstack.UuidGenerator
import com.github.zsoltk.composeribs.core.routing.source.backstack.current

/**
 * Operation:
 *
 * [A, B, C] + Replace(D) = [A, B, D]
 */
internal class Replace<T: Any>(
    private val element: T
) : BackStack.Operation<T> {

    override fun isApplicable(elements: List<BackStackElement<T>>): Boolean =
        element != elements.current?.key?.routing

    override fun invoke(
        elements: List<BackStackElement<T>>,
        uuidGenerator: UuidGenerator
    ): List<BackStackElement<T>> = elements.dropLast(1) + BackStackElement(
        key = BackStack.LocalRoutingKey(element, uuidGenerator.incrementAndGet()),
        fromState = BackStack.TransitionState.CREATED,
        targetState = BackStack.TransitionState.ON_SCREEN,
    )
}

fun <T: Any> BackStack<T>.replace(element: T) {
    perform(Replace(element))
}
