package com.bumble.appyx.transitionmodel.backstack.operation

import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.RawValue
import com.bumble.appyx.interactions.core.BaseOperation
import com.bumble.appyx.interactions.core.Operation
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.transitionmodel.backstack.BackStack
import com.bumble.appyx.transitionmodel.backstack.BackStackModel

/**
 * Operation:
 *
 * [A, B, C] + NewRoot(D) = [ D ]
 */
@Parcelize
data class NewRoot<NavTarget>(
    private val navTarget: @RawValue NavTarget,
    override val mode: Operation.Mode = Operation.Mode.KEYFRAME
) : BaseOperation<BackStackModel.State<NavTarget>>() {
    override fun isApplicable(state: BackStackModel.State<NavTarget>): Boolean =
        state.stashed.size + 1 > 1 || state.active.navTarget != navTarget

    override fun createFromState(baseLineState: BackStackModel.State<NavTarget>): BackStackModel.State<NavTarget> =
        baseLineState.copy(
            created = baseLineState.created + navTarget.asElement()
        )

    override fun createTargetState(fromState: BackStackModel.State<NavTarget>): BackStackModel.State<NavTarget> =
        fromState.copy(
            active = fromState.created.last(),
            created = fromState.created.dropLast(1),
            destroyed = fromState.destroyed + fromState.stashed,
            stashed = emptyList()
        )
}

fun <NavTarget : Any> BackStack<NavTarget>.newRoot(
    navTarget: NavTarget,
    mode: Operation.Mode = Operation.Mode.KEYFRAME
) {
    operation(NewRoot(navTarget, mode))
}
