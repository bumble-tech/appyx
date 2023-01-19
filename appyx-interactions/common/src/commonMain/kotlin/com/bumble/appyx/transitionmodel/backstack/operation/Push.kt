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
 * [A, B, C] + Push(D) = [A, B, C, D]
 */
@Parcelize
data class Push<NavTarget : Any>(
    private val navTarget: @RawValue NavTarget
) : BackStackOperation<NavTarget> {
    override fun isApplicable(state: BackStackModel.State<NavTarget>): Boolean =
        navTarget != state.active.navTarget

    override fun invoke(baseLineState: BackStackModel.State<NavTarget>): NavTransition<BackStackModel.State<NavTarget>> {
        val fromState = baseLineState.copy(
            created = baseLineState.created + navTarget.asElement()
        )
        val targetState = fromState.copy(
            active = fromState.created.last(),
            created = fromState.created.dropLast(1),
            stashed = listOf(fromState.active) + fromState.stashed,
        )
        return NavTransition(
            fromState = fromState,
            targetState = targetState
        )
    }
}

fun <NavTarget : Any> BackStack<NavTarget>.push(navTarget: NavTarget) {
    operation(Push(navTarget))
}
