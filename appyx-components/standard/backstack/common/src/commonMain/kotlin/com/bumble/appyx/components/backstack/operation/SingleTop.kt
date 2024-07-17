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
 * [A, B, C, D] + SingleTop(B) = [A, B]          // same in stashed, acts as n * Pop
 * [A, B, C, D] + SingleTop(D) = [A, B, C, D]    // same active, no op
 * [A, B, C, D] + SingleTop(E) = [A, B, C, D, E] // not found, acts as Push
 */
@Parcelize
class SingleTop<NavTarget : Any>(
    private val navTarget: @RawValue NavTarget,
    override var mode: Operation.Mode = Operation.Mode.KEYFRAME
) : BaseOperation<BackStackModel.State<NavTarget>>() {

    override fun isApplicable(state: BackStackModel.State<NavTarget>): Boolean = true

    override fun createFromState(baseLineState: BackStackModel.State<NavTarget>): BackStackModel.State<NavTarget> =
        if (baseLineState.active.interactionTarget == navTarget || baseLineState.stashed.any { it.interactionTarget == navTarget }) {
            baseLineState
        } else {
            baseLineState.copy(created = baseLineState.created + navTarget.asElement())
        }

    override fun createTargetState(fromState: BackStackModel.State<NavTarget>): BackStackModel.State<NavTarget> {
        val elements = fromState.stashed + fromState.active + fromState.created
        val trailing = elements.takeLastWhile { it.interactionTarget != navTarget }
        val active = elements[elements.size - trailing.size - 1]
        return fromState.copy(
            active = active,
            stashed = elements.take(elements.size - trailing.size - 1),
            destroyed = fromState.destroyed + trailing
        )
    }

    override fun equals(other: Any?): Boolean = other != null && (this::class == other::class)

    override fun hashCode(): Int = this::class.hashCode()
}

fun <NavTarget : Any> BackStack<NavTarget>.singleTop(
    navTarget: NavTarget,
    mode: Operation.Mode = Operation.Mode.KEYFRAME,
    animationSpec: AnimationSpec<Float>? = null
) {
    operation(operation = SingleTop(navTarget, mode), animationSpec = animationSpec)
}
