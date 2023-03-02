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
data class AddFirst<InteractionTarget>(
    private val element: @RawValue InteractionTarget,
    override val mode: Operation.Mode = Operation.Mode.KEYFRAME
) : BaseOperation<PromoterModel.State<InteractionTarget>>() {

    override fun isApplicable(state: PromoterModel.State<InteractionTarget>): Boolean =
        true

    override fun createFromState(baseLineState: PromoterModel.State<InteractionTarget>): PromoterModel.State<InteractionTarget> =
        baseLineState.copy(
            elements = listOf(element.asElement() to PromoterModel.State.ElementState.CREATED) + baseLineState.elements,
        )

    override fun createTargetState(fromState: PromoterModel.State<InteractionTarget>): PromoterModel.State<InteractionTarget> =
        fromState.copy(
            elements = fromState.elements.map { it.first to it.second.next() }
        )
}

fun <InteractionTarget : Any> Promoter<InteractionTarget>.addFirst(
    interactionTarget: InteractionTarget,
    mode: Operation.Mode = Operation.Mode.KEYFRAME,
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec
) {
    operation(AddFirst(interactionTarget, mode), animationSpec)
}
