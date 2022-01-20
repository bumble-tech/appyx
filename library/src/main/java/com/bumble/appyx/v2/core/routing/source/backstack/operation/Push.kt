package com.bumble.appyx.v2.core.routing.source.backstack.operation

import com.bumble.appyx.v2.core.routing.RoutingKey
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack
import com.bumble.appyx.v2.core.routing.source.backstack.BackStackElement
import com.bumble.appyx.v2.core.routing.source.backstack.BackStackElements
import com.bumble.appyx.v2.core.routing.source.backstack.current
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * Operation:
 *
 * [A, B, C] + Push(D) = [A, B, C, D]
 */
@Parcelize
data class Push<T : Any>(
    private val element: @RawValue T
) : BackStackOperation<T> {

    override fun isApplicable(elements: BackStackElements<T>): Boolean =
        element != elements.current?.key?.routing

    override fun invoke(elements: BackStackElements<T>): BackStackElements<T> {
        return elements.map {
            if (it.targetState == BackStack.TransitionState.ACTIVE) {
                it.transitionTo(
                    targetState = BackStack.TransitionState.STASHED_IN_BACK_STACK,
                    operation = this
                )
            } else {
                it
            }
        } + BackStackElement(
            key = RoutingKey(element),
            fromState = BackStack.TransitionState.CREATED,
            targetState = BackStack.TransitionState.ACTIVE,
            operation = this
        )
    }
}

fun <T : Any> BackStack<T>.push(element: T) {
    accept(Push(element))
}
