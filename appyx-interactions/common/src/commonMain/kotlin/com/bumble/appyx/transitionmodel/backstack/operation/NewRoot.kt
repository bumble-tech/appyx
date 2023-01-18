package com.bumble.appyx.transitionmodel.backstack.operation

import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.RawValue
import com.bumble.appyx.interactions.core.NavElement
import com.bumble.appyx.interactions.core.NavElements
import com.bumble.appyx.interactions.core.NavKey
import com.bumble.appyx.interactions.core.NavTransition
import com.bumble.appyx.transitionmodel.backstack.BackStack
import com.bumble.appyx.transitionmodel.backstack.BackStackModel
import com.bumble.appyx.transitionmodel.backstack.active
import com.bumble.appyx.transitionmodel.backstack.BackStackModel.State.ACTIVE
import com.bumble.appyx.transitionmodel.backstack.BackStackModel.State.CREATED

/**
 * Operation:
 *
 * [A, B, C] + NewRoot(D) = [ D ]
 */
@Parcelize
data class NewRoot<NavTarget : Any>(
    private val navTarget: @RawValue NavTarget
) :  BackStackOperation<NavTarget> {

    override fun isApplicable(elements: NavElements<NavTarget, BackStackModel.State>): Boolean =
        elements.size > 1 || elements.first().key != navTarget

    override fun invoke(elements: NavElements<NavTarget, BackStackModel.State>): NavTransition<NavTarget, BackStackModel.State> {
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
                        else -> BackStackModel.State.DROPPED
                    },
                    operation = this
                )
            }
        )
    }
}

fun <NavTarget : Any> BackStack<NavTarget>.newRoot(navTarget: NavTarget) {
    operation(NewRoot(navTarget))
}
