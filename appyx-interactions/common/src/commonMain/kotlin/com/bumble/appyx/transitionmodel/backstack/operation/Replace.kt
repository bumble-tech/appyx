package com.bumble.appyx.transitionmodel.backstack.operation

import com.bumble.appyx.interactions.core.NavKey
import com.bumble.appyx.interactions.core.NavElement
import com.bumble.appyx.interactions.core.NavElements
import com.bumble.appyx.interactions.core.NavTransition
import com.bumble.appyx.transitionmodel.backstack.BackStack
import com.bumble.appyx.transitionmodel.backstack.BackStack.State
import com.bumble.appyx.transitionmodel.backstack.BackStack.State.ACTIVE
import com.bumble.appyx.transitionmodel.backstack.BackStack.State.CREATED
import com.bumble.appyx.transitionmodel.backstack.activeElement
import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.RawValue


/**
 * Operation:
 *
 * [A, B, C] + Replace(D) = [A, B, D]
 */
@Parcelize
data class Replace<NavTarget : Any>(
    private val target: @RawValue NavTarget
) : BackStackOperation<NavTarget> {

    override fun isApplicable(elements: NavElements<NavTarget, State>): Boolean =
        target != elements.activeElement

    override fun invoke(elements: NavElements<NavTarget, State>): NavTransition<NavTarget, State> {
        require(elements.any { it.state == ACTIVE }) { "No element to be replaced, state=$elements" }

        val fromState = elements + NavElement(
            key = NavKey(target),
            fromState = CREATED,
            targetState = CREATED,
            operation = this
        )
        return NavTransition(
            fromState = fromState,
            targetState = fromState.mapIndexed { index, element ->
                element.transitionTo(
                    newTargetState = when (index) {
                        fromState.lastIndex - 1 -> State.REPLACED
                        fromState.lastIndex -> ACTIVE
                        else -> element.state
                    },
                    operation = this
                )
            }
        )
    }
}

fun <NavTarget : Any> BackStack<NavTarget>.replace(target: NavTarget) {
    enqueue(Replace(target))
}
