package com.bumble.appyx.routingsource.backstack.operation

import com.bumble.appyx.core.routing.RoutingKey
import com.bumble.appyx.routingsource.backstack.BackStack
import com.bumble.appyx.routingsource.backstack.BackStackElement
import com.bumble.appyx.routingsource.backstack.BackStackElements
import com.bumble.appyx.routingsource.backstack.activeRouting
import com.bumble.appyx.routingsource.backstack.BackStack.TransitionState.ACTIVE
import com.bumble.appyx.routingsource.backstack.BackStack.TransitionState.CREATED
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
        element != elements.activeRouting

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
            fromState = CREATED,
            targetState = ACTIVE,
            operation = this
        )
    }
}

fun <T : Any> BackStack<T>.push(element: T) {
    accept(Push(element))
}
