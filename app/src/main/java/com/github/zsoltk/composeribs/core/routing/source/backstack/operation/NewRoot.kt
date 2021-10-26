package com.github.zsoltk.composeribs.core.routing.source.backstack.operation

import com.github.zsoltk.composeribs.core.routing.source.backstack.*
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.Operation

/**
 * Operation:
 *
 * [A, B, C] + NewRoot(D) = [ D ]
 */
internal class NewRoot<T : Any>(
    private val element: T
) : Operation<T> {

    override fun isApplicable(elements: Elements<T>): Boolean =
        !(elements.size == 1 && elements.current?.key?.routing == element)

    override fun invoke(
        elements: Elements<T>,
        uuidGenerator: UuidGenerator
    ): Elements<T> {
        val last = elements.lastOrNull()?.copy(targetState = BackStack.TransitionState.DESTROYED)

        return listOfNotNull(
            last,
            BackStackElement(
                key = BackStack.LocalRoutingKey(element, uuidGenerator.incrementAndGet()),
                fromState = BackStack.TransitionState.CREATED,
                targetState = BackStack.TransitionState.ON_SCREEN,
            )
        )
    }
}

fun <T : Any> BackStack<T>.newRoot(element: T) {
    perform(NewRoot(element))
}
