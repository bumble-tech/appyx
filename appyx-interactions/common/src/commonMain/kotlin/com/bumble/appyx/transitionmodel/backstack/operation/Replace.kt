package com.bumble.appyx.transitionmodel.backstack.operation

import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.RawValue
import com.bumble.appyx.interactions.core.NavTransition
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.transitionmodel.backstack.BackStack
import com.bumble.appyx.transitionmodel.backstack.BackStackModel.State


/**
 * Operation:
 *
 * [A, B, C] + Replace(D) = [A, B, D]
 */
@Parcelize
data class Replace<NavTarget : Any>(
    private val navTarget: @RawValue NavTarget
) : BackStackOperation<NavTarget> {
    override fun isApplicable(state: State<NavTarget>): Boolean =
        navTarget != state.active.navTarget

    override fun invoke(baseLineState: State<NavTarget>): NavTransition<State<NavTarget>> {
        val fromState = baseLineState.copy(
            created = baseLineState.created + navTarget.asElement()
        )
        val targetState = fromState.copy(
            active = fromState.created.last(),
            created = fromState.created.dropLast(1),
            destroyed = fromState.destroyed + fromState.active
        )

        return NavTransition(
            fromState = fromState,
            targetState = targetState
        )
    }
}

fun <NavTarget : Any> BackStack<NavTarget>.replace(target: NavTarget) {
    operation(Replace(target))
}
