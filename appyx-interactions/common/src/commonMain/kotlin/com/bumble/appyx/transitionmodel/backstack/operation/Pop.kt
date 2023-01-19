package com.bumble.appyx.transitionmodel.backstack.operation

import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.core.NavTransition
import com.bumble.appyx.transitionmodel.backstack.BackStack
import com.bumble.appyx.transitionmodel.backstack.BackStackModel.State

/**
 * Operation:
 *
 * [A, B, C] + Pop = [A, B]
 */
@Parcelize
class Pop<NavTarget : Any> : BackStackOperation<NavTarget> {
    override fun isApplicable(state: State<NavTarget>): Boolean =
        state.stashed.isNotEmpty()

    override fun invoke(baseLineState: State<NavTarget>): NavTransition<State<NavTarget>> {
        val fromState = baseLineState

        val targetState = fromState.copy(
            active = fromState.stashed.first(),
            destroyed = fromState.destroyed + fromState.active,
            stashed = fromState.stashed.subList(1, fromState.stashed.size)
        )
        return NavTransition(
            fromState = fromState,
            targetState = targetState
        )
    }

    override fun equals(other: Any?): Boolean = this.javaClass == other?.javaClass

    override fun hashCode(): Int = this.javaClass.hashCode()
}

fun <NavTarget : Any> BackStack<NavTarget>.pop() {
    operation(Pop())
}
