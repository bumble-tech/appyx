package com.bumble.appyx.navmodel2.backstack.operation

import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.core.navigation2.NavElement
import com.bumble.appyx.core.navigation2.NavElements
import com.bumble.appyx.core.navigation2.NavTransition
import com.bumble.appyx.navmodel2.backstack.BackStack
import com.bumble.appyx.navmodel2.backstack.BackStack.State.ACTIVE
import com.bumble.appyx.navmodel2.backstack.BackStack.State.CREATED
import com.bumble.appyx.navmodel2.backstack.BackStackElements
import com.bumble.appyx.navmodel2.backstack.activeElement
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * Operation:
 *
 * [A, B, C] + Replace(D) = [A, B, D]
 */
@Parcelize
data class Replace<NavTarget : Any>(
    private val target: @RawValue NavTarget
) : BackStackOperation<NavTarget> {

    override fun isApplicable(elements: BackStackElements<NavTarget>): Boolean =
        target != elements.activeElement

    override fun invoke(elements: NavElements<NavTarget, BackStack.State>): NavTransition<NavTarget, BackStack.State> {
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
                        fromState.lastIndex - 1 -> BackStack.State.REPLACED
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
