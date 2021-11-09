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
 * [A, B, C] + Push(D) = [A, B, C, D]
 */
data class Push<T : Any>(
    private val element: T
) : BackStackOperation<T> {

    override fun isApplicable(elements: BackStackElements<T>): Boolean =
        element != elements.current?.key?.routing

    override fun invoke(
        elements: BackStackElements<T>,
        uuidGenerator: UuidGenerator
    ): BackStackElements<T> {
        return elements.map {
            if (it.targetState == BackStack.TransitionState.ON_SCREEN) {
                it.copy(targetState = BackStack.TransitionState.STASHED_IN_BACK_STACK)
            } else {
                it
            }
        } + BackStackElement(
            key = BackStack.LocalRoutingKey(element, uuidGenerator.incrementAndGet()),
            fromState = BackStack.TransitionState.CREATED,
            targetState = BackStack.TransitionState.ON_SCREEN,
            operation = this
        )
    }
}

fun <T : Any> BackStack<T>.push(element: T) {
    perform(Push(element))
}
