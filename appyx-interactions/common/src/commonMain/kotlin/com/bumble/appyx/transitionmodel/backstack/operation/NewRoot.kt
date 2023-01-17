package com.bumble.appyx.transitionmodel.backstack.operation

import com.bumble.appyx.interactions.core.NavElement
import com.bumble.appyx.interactions.core.NavElements
import com.bumble.appyx.interactions.core.NavKey
import com.bumble.appyx.interactions.core.NavTransition
import com.bumble.appyx.interactions.core.Operation
import com.bumble.appyx.transitionmodel.backstack.BackStack
import com.bumble.appyx.transitionmodel.backstack.active
import com.bumble.appyx.transitionmodel.backstack.BackStack.State.ACTIVE
import com.bumble.appyx.transitionmodel.backstack.BackStack.State.CREATED

/**
 * Operation:
 *
 * [A, B, C] + NewRoot(D) = [ D ]
 */
// FIXME @Parcelize
data class NewRoot<NavTarget : Any>(
    private val navTarget: NavTarget // FIXME @RawValue
) : Operation<NavTarget, BackStack.State> {

    override fun isApplicable(elements: NavElements<NavTarget, BackStack.State>): Boolean =
        elements.size > 1 || elements.first().key != navTarget

    override fun invoke(elements: NavElements<NavTarget, BackStack.State>): NavTransition<NavTarget, BackStack.State> {
        val current = elements.active
        requireNotNull(current) { "No previous elements, state=$elements" }

        val fromState = elements + NavElement(
            key = NavKey(navTarget),
            fromState = CREATED,
            targetState = CREATED,
            operation = this
        )
        return NavTransition(
            fromState = fromState,
            targetState = fromState.mapIndexed { index, element ->
                element.transitionTo(
                    newTargetState = when (index) {
                        fromState.lastIndex -> ACTIVE
                        else -> BackStack.State.DROPPED
                    },
                    operation = this
                )
            }
        )
    }
}

fun <NavTarget : Any> BackStack<NavTarget>.newRoot(navTarget: NavTarget) {
    enqueue(NewRoot(navTarget))
}
