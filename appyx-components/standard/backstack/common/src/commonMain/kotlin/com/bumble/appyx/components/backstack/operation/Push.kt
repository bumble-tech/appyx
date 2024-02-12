package com.bumble.appyx.components.backstack.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.interactions.core.model.transition.BaseOperation
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.utils.multiplatform.Parcelize
import com.bumble.appyx.utils.multiplatform.RawValue

/**
 * Operation:
 *
 * [A, B, C] + Push(D) = [A, B, C, D]
 */
@Parcelize
data class Push<NavTarget : Any>(
    private val navTarget: @RawValue NavTarget,
    override var mode: Operation.Mode = Operation.Mode.KEYFRAME
) : BaseOperation<BackStackModel.State<NavTarget>>() {

    override fun isApplicable(state: BackStackModel.State<NavTarget>): Boolean =
        navTarget != state.active.interactionTarget

    override fun createFromState(
        baseLineState: BackStackModel.State<NavTarget>
    ): BackStackModel.State<NavTarget> =
        baseLineState.copy(
            created = baseLineState.created + navTarget.asElement()
        )

    override fun createTargetState(
        fromState: BackStackModel.State<NavTarget>
    ): BackStackModel.State<NavTarget> =
        fromState.copy(
            active = fromState.created.last(),
            created = fromState.created.dropLast(1),
            stashed = fromState.stashed + fromState.active,
        )
}

fun <NavTarget : Any> BackStack<NavTarget>.push(
    navTarget: NavTarget,
    mode: Operation.Mode = Operation.Mode.KEYFRAME,
    animationSpec: AnimationSpec<Float>? = null
) {
    operation(operation = Push(navTarget, mode), animationSpec = animationSpec)
}
