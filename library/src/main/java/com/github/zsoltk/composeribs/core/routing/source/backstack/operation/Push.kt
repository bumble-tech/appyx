package com.github.zsoltk.composeribs.core.routing.source.backstack.operation

import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.Operation
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackElement
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackElements
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackOnScreenResolver
import com.github.zsoltk.composeribs.core.routing.source.backstack.current

/**
 * Operation:
 *
 * [A, B, C] + Push(D) = [A, B, C, D]
 */
internal class Push<T : Any>(
    private val element: T
) : Operation<T> {

    override fun isApplicable(elements: BackStackElements<T>): Boolean =
        element != elements.current?.key?.routing

    override fun invoke(elements: BackStackElements<T>): BackStackElements<T> {
        return elements.map {
            if (it.targetState == BackStack.TransitionState.ON_SCREEN) {
                it.transitionTo(targetState = BackStack.TransitionState.STASHED_IN_BACK_STACK)
            } else {
                it
            }
        } + BackStackElement(
            onScreenResolver = BackStackOnScreenResolver,
            key = RoutingKey(element),
            fromState = BackStack.TransitionState.CREATED,
            targetState = BackStack.TransitionState.ON_SCREEN,
            onScreen = true
        )
    }
}

fun <T : Any> BackStack<T>.push(element: T) {
    perform(Push(element))
}
