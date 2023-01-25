package com.bumble.appyx.transitionmodel.backstack.operation

import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.RawValue
import com.bumble.appyx.interactions.core.BaseOperation
import com.bumble.appyx.interactions.core.Operation
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
    private val navTarget: @RawValue NavTarget,
    override val mode: Operation.Mode = Operation.Mode.KEYFRAME
) : BaseOperation<State<NavTarget>>() {
    override fun isApplicable(state: State<NavTarget>): Boolean =
        navTarget != state.active.navTarget

    override fun createFromState(baseLineState: State<NavTarget>): State<NavTarget> =
        baseLineState.copy(
            created = baseLineState.created + navTarget.asElement()
        )

    override fun createTargetState(fromState: State<NavTarget>): State<NavTarget> =
        fromState.copy(
            active = fromState.created.last(),
            created = fromState.created.dropLast(1),
            destroyed = fromState.destroyed + fromState.active
        )
}

fun <NavTarget : Any> BackStack<NavTarget>.replace(
    target: NavTarget,
    mode: Operation.Mode = Operation.Mode.KEYFRAME
) {
    operation(Replace(target, mode))
}
