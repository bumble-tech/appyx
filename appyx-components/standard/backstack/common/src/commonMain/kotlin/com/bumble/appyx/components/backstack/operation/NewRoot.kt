package com.bumble.appyx.components.backstack.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.interactions.model.asElement
import com.bumble.appyx.interactions.model.transition.BaseOperation
import com.bumble.appyx.interactions.model.transition.Operation
import com.bumble.appyx.utils.multiplatform.Parcelize
import com.bumble.appyx.utils.multiplatform.RawValue

/**
 * Operation:
 *
 * [A, B, C] + NewRoot(D) = [ D ]
 */
@Parcelize
data class NewRoot<NavTarget>(
    private val navTarget: @RawValue NavTarget,
    override var mode: Operation.Mode = Operation.Mode.KEYFRAME
) : BaseOperation<BackStackModel.State<NavTarget>>() {
    override fun isApplicable(state: BackStackModel.State<NavTarget>): Boolean =
        true

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
            destroyed = fromState.destroyed + fromState.stashed,
            stashed = emptyList()
        )
}

fun <NavTarget : Any> BackStack<NavTarget>.newRoot(
    navTarget: NavTarget,
    mode: Operation.Mode = Operation.Mode.KEYFRAME,
    animationSpec: AnimationSpec<Float>? = null
) {
    operation(operation = NewRoot(navTarget, mode), animationSpec = animationSpec)
}
