package com.bumble.appyx.components.backstack.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel.State
import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.core.model.transition.BaseOperation
import com.bumble.appyx.interactions.core.model.transition.Operation

/**
 * Operation:
 *
 * [A, B, C] + Pop = [A, B]
 */
@Parcelize
class Pop<InteractionTarget : Any>(
    override val mode: Operation.Mode = Operation.Mode.KEYFRAME
) : BaseOperation<State<InteractionTarget>>() {
    override fun isApplicable(state: State<InteractionTarget>): Boolean =
        state.stashed.isNotEmpty()

    override fun createFromState(baseLineState: State<InteractionTarget>): State<InteractionTarget> =
        baseLineState


    override fun createTargetState(fromState: State<InteractionTarget>): State<InteractionTarget> =
        fromState.copy(
            active = fromState.stashed.last(),
            destroyed = fromState.destroyed + fromState.active,
            stashed = fromState.stashed.dropLast(1)
        )

    override fun equals(other: Any?): Boolean = other != null && (this::class == other::class)

    override fun hashCode(): Int = this::class.hashCode()
}

fun <InteractionTarget : Any> BackStack<InteractionTarget>.pop(
    mode: Operation.Mode = Operation.Mode.KEYFRAME,
    animationSpec: AnimationSpec<Float>? = null
) {
    operation(operation = Pop(mode), animationSpec = animationSpec)
}
