package com.bumble.appyx.transitionmodel.backstack.operation

import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.RawValue
import com.bumble.appyx.interactions.core.NavTransition
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.transitionmodel.backstack.BackStack
import com.bumble.appyx.transitionmodel.backstack.BackStackModel

/**
 * Operation:
 *
 * [A, B, C] + NewRoot(D) = [ D ]
 */
@Parcelize
data class NewRoot<NavTarget : Any>(
    private val navTarget: @RawValue NavTarget
) : BackStackOperation<NavTarget> {
    override fun isApplicable(state: BackStackModel.State<NavTarget>): Boolean =
        state.stashed.size + 1 > 1 || state.active.navTarget != navTarget

    override fun invoke(baseLineState: BackStackModel.State<NavTarget>): NavTransition<BackStackModel.State<NavTarget>> {
        val fromState = baseLineState.copy(
            created = baseLineState.created + navTarget.asElement()
        )
        val targetState = fromState.copy(
            active = fromState.created.last(),
            created = fromState.created.dropLast(1),
            destroyed = fromState.destroyed + fromState.stashed,
            stashed = emptyList()
        )
        return NavTransition(
            fromState = fromState,
            targetState = targetState
        )
    }
}

fun <NavTarget : Any> BackStack<NavTarget>.newRoot(navTarget: NavTarget) {
    operation(NewRoot(navTarget))
}
