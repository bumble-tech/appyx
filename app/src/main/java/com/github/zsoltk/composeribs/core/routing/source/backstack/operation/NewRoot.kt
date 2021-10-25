package com.github.zsoltk.composeribs.core.routing.source.backstack.operation

import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.Operation
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackElement
import com.github.zsoltk.composeribs.core.routing.source.backstack.UuidGenerator
import com.github.zsoltk.composeribs.core.routing.source.backstack.current

/**
 * Operation:
 *
 * [A, B, C] + NewRoot(D) = [ D ]
 */
internal class NewRoot<T : Any>(
    private val element: T
) : Operation<T> {

    override fun isApplicable(elements: List<BackStackElement<T>>): Boolean =
        !(elements.size == 1 && elements.current?.key?.routing == element)

    override fun invoke(
        elements: List<BackStackElement<T>>,
        uuidGenerator: UuidGenerator
    ): List<BackStackElement<T>> = listOf(
        BackStackElement(
            key = BackStack.LocalRoutingKey(element, uuidGenerator.incrementAndGet()),
            fromState = BackStack.TransitionState.CREATED,
            targetState = BackStack.TransitionState.ON_SCREEN,
        )
    )
}

fun <T : Any> BackStack<T>.newRoot(element: T) {
    perform(NewRoot(element))
}
