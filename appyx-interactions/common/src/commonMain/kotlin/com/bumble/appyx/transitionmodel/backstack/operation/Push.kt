package com.bumble.appyx.transitionmodel.backstack.operation

import com.bumble.appyx.interactions.core.navigation.NavKey
import com.bumble.appyx.interactions.core.navigation2.NavElement
import com.bumble.appyx.interactions.core.navigation2.NavElements
import com.bumble.appyx.interactions.core.navigation2.NavTransition
import com.bumble.appyx.transitionmodel.backstack.BackStack
import com.bumble.appyx.transitionmodel.backstack.BackStack.State
import com.bumble.appyx.transitionmodel.backstack.BackStack.State.ACTIVE
import com.bumble.appyx.transitionmodel.backstack.BackStack.State.CREATED
import com.bumble.appyx.transitionmodel.backstack.BackStack.State.STASHED
import com.bumble.appyx.transitionmodel.backstack.activeElement
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * Operation:
 *
 * [A, B, C] + Push(D) = [A, B, C, D]
 */
@Parcelize
data class Push<NavTarget : Any>(
    private val navTarget: @RawValue NavTarget
) : BackStackOperation<NavTarget> {

    override fun isApplicable(elements: NavElements<NavTarget, State>): Boolean =
        navTarget != elements.activeElement

    override fun invoke(elements: NavElements<NavTarget, State>): NavTransition<NavTarget, State> {
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
                        fromState.lastIndex - 1 -> STASHED
                        fromState.lastIndex -> ACTIVE
                        else -> element.state
                    },
                    operation = this
                )
            }
        )
    }

}

fun <NavTarget : Any> BackStack<NavTarget>.push(navTarget: NavTarget) {
    enqueue(Push(navTarget))
}
