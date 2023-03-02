package com.bumble.appyx.transitionmodel.promoter.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.RawValue
import com.bumble.appyx.interactions.core.model.transition.BaseOperation
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.transitionmodel.promoter.Promoter
import com.bumble.appyx.transitionmodel.promoter.PromoterModel

@Parcelize
data class AddFirst<NavTarget>(
    private val element: @RawValue NavTarget,
    override val mode: Operation.Mode = Operation.Mode.KEYFRAME
) : BaseOperation<PromoterModel.State<NavTarget>>() {

    override fun isApplicable(state: PromoterModel.State<NavTarget>): Boolean =
        true

    override fun createFromState(baseLineState: PromoterModel.State<NavTarget>): PromoterModel.State<NavTarget> =
        baseLineState.copy(
            elements = listOf(element.asElement() to PromoterModel.State.ElementState.CREATED) + baseLineState.elements,
        )

    override fun createTargetState(fromState: PromoterModel.State<NavTarget>): PromoterModel.State<NavTarget> =
        fromState.copy(
            elements = fromState.elements.map { it.first to it.second.next() }
        )
}

fun <NavTarget : Any> Promoter<NavTarget>.addFirst(
    navTarget: NavTarget,
    mode: Operation.Mode = Operation.Mode.KEYFRAME,
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec
) {
    operation(AddFirst(navTarget, mode), animationSpec)
}
