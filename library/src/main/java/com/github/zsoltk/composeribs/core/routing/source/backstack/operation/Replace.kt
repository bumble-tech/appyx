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

    override fun isApplicable(elements: BackStackElements<T>): Boolean =
        element != elements.current?.key?.routing

    override fun invoke(
        elements: BackStackElements<T>,
        uuidGenerator: UuidGenerator
    ): BackStackElements<T> {
        require(elements.any { it.targetState == BackStack.TransitionState.ON_SCREEN }) { "No element to be replaced, state=$elements" }

        return elements.mapIndexed { index, element ->
            if (index == elements.currentIndex) {
                element.copy(targetState = BackStack.TransitionState.DESTROYED)
            } else {
                element
            }
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
