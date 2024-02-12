package com.bumble.appyx.components.backstack.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel.State
import com.bumble.appyx.interactions.model.transition.BaseOperation
import com.bumble.appyx.interactions.model.transition.Operation
import com.bumble.appyx.utils.multiplatform.Parcelize

/**
 * Operation:
 *
 * [A, B, C] + Pop = [A, B]
 */
@Parcelize
class Pop<NavTarget : Any>(
    override var mode: Operation.Mode = Operation.Mode.KEYFRAME
) : BaseOperation<State<NavTarget>>() {
    override fun isApplicable(state: State<NavTarget>): Boolean =
        state.stashed.isNotEmpty()

    override fun createFromState(baseLineState: State<NavTarget>): State<NavTarget> =
        baseLineState


    override fun createTargetState(fromState: State<NavTarget>): State<NavTarget> =
        fromState.copy(
            active = fromState.stashed.last(),
            destroyed = fromState.destroyed + fromState.active,
            stashed = fromState.stashed.dropLast(1)
        )

    override fun equals(other: Any?): Boolean = other != null && (this::class == other::class)

    override fun hashCode(): Int = this::class.hashCode()
}

fun <NavTarget : Any> BackStack<NavTarget>.pop(
    mode: Operation.Mode = Operation.Mode.KEYFRAME,
    animationSpec: AnimationSpec<Float>? = null
) {
    operation(operation = Pop(mode), animationSpec = animationSpec)
}
