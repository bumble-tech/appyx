package com.bumble.appyx.transitionmodel.backstack.operation

import com.bumble.appyx.interactions.core.NavKey
import com.bumble.appyx.interactions.core.NavElement
import com.bumble.appyx.interactions.core.NavElements
import com.bumble.appyx.interactions.core.NavTransition
import com.bumble.appyx.transitionmodel.backstack.BackStackModel
import com.bumble.appyx.transitionmodel.backstack.BackStackModel.State
import com.bumble.appyx.transitionmodel.backstack.BackStackModel.State.ACTIVE
import com.bumble.appyx.transitionmodel.backstack.BackStackModel.State.CREATED
import com.bumble.appyx.transitionmodel.backstack.BackStackModel.State.STASHED
import com.bumble.appyx.transitionmodel.backstack.activeElement
import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.RawValue
import com.bumble.appyx.transitionmodel.backstack.BackStack

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
    operation(Push(navTarget))
}
