package com.bumble.appyx.navmodel2.backstack.operation

import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.core.navigation2.NavElement
import com.bumble.appyx.core.navigation2.NavElements
import com.bumble.appyx.core.navigation2.NavTransition
import com.bumble.appyx.core.navigation2.Operation
import com.bumble.appyx.navmodel2.backstack.BackStack
import com.bumble.appyx.navmodel2.backstack.active
import com.bumble.appyx.navmodel2.backstack.BackStack.State.ACTIVE
import com.bumble.appyx.navmodel2.backstack.BackStack.State.CREATED
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * Operation:
 *
 * [A, B, C] + NewRoot(D) = [ D ]
 */
@Parcelize
data class NewRoot<NavTarget : Any>(
    private val navTarget: @RawValue NavTarget
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
