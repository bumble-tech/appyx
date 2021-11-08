package com.github.zsoltk.composeribs.core.routing.source.backstack.operation

import com.github.zsoltk.composeribs.core.routing.UuidGenerator
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackElement
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackElements
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackOperation
import com.github.zsoltk.composeribs.core.routing.source.backstack.current

/**
 * Operation:
 *
 * [A, B, C] + NewRoot(D) = [ D ]
 */
class NewRoot<T : Any>(
    private val element: T
) : BackStackOperation<T> {

    override fun isApplicable(elements: BackStackElements<T>): Boolean = true

    override fun invoke(
        elements: BackStackElements<T>,
        uuidGenerator: UuidGenerator
    ): BackStackElements<T> {

        val current = elements.current
        requireNotNull(current) { "No previous elements, state=$elements" }

        return if (current.key.routing == element) {
            listOf(current)
        } else {
            listOf(
                current.copy(targetState = BackStack.TransitionState.DESTROYED),
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
