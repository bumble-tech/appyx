package com.bumble.appyx.routingsource.backstack.operation

import com.bumble.appyx.core.routing.RoutingKey
import com.bumble.appyx.routingsource.backstack.BackStack
import com.bumble.appyx.routingsource.backstack.BackStackElement
import com.bumble.appyx.routingsource.backstack.BackStackElements
import com.bumble.appyx.routingsource.backstack.active
import com.bumble.appyx.routingsource.backstack.BackStack.TransitionState.ACTIVE
import com.bumble.appyx.routingsource.backstack.BackStack.TransitionState.CREATED
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * Operation:
 *
 * [A, B, C] + NewRoot(D) = [ D ]
 */
@Parcelize
data class NewRoot<T : Any>(
    private val element: @RawValue T
) : BackStackOperation<T> {

    override fun isApplicable(elements: BackStackElements<T>): Boolean = true

    override fun invoke(
        elements: BackStackElements<T>,
    ): BackStackElements<T> {

        val current = elements.active
        requireNotNull(current) { "No previous elements, state=$elements" }

        return if (current.key.routing == element) {
            listOf(current)
        } else {
            listOf(
                current.transitionTo(
                    newTargetState = BackStack.TransitionState.DESTROYED,
                    operation = this
                ),
                BackStackElement(
                    key = RoutingKey(element),
                    fromState = CREATED,
                    targetState = ACTIVE,
                    operation = this
                )
            )
        }
    }
}

fun <T : Any> BackStack<T>.newRoot(element: T) {
    accept(NewRoot(element))
}
