package com.github.zsoltk.composeribs.core.routing.source.backstack.operation

import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.Operation
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackElement
import com.github.zsoltk.composeribs.core.routing.source.backstack.Elements
import com.github.zsoltk.composeribs.core.routing.source.backstack.UuidGenerator

/**
 * Operation:
 *
 * [A, B, C] + NewRoot(D) = [ D ]
 */
internal class NewRoot<T : Any>(
    private val element: T
) : Operation<T> {

    override fun isApplicable(elements: Elements<T>): Boolean = true

    override fun invoke(
        elements: Elements<T>,
        uuidGenerator: UuidGenerator
    ): Elements<T> {

        val last = elements.lastOrNull()
        requireNotNull(last) { "No previous elements, state=$elements" }

        return if (last.key.routing == element) {
            listOf(elements.last())
        } else {
            listOf(
                last.copy(targetState = BackStack.TransitionState.DESTROYED),
                BackStackElement(
                    key = BackStack.LocalRoutingKey(element, uuidGenerator.incrementAndGet()),
                    fromState = BackStack.TransitionState.CREATED,
                    targetState = BackStack.TransitionState.ON_SCREEN,
                )
            )
        }
    }
}

fun <T : Any> BackStack<T>.newRoot(element: T) {
    perform(NewRoot(element))
}
