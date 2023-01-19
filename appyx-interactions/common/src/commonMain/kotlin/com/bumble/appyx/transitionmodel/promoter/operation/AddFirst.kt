package com.bumble.appyx.transitionmodel.promoter.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.RawValue
import com.bumble.appyx.interactions.core.BaseOperation
import com.bumble.appyx.interactions.core.asElements
import com.bumble.appyx.transitionmodel.promoter.Promoter
import com.bumble.appyx.transitionmodel.promoter.PromoterModel

@Parcelize
data class AddFirst<NavTarget>(
    private val element: @RawValue NavTarget
) : BaseOperation<PromoterModel.State<NavTarget>>() {

    override fun isApplicable(state: PromoterModel.State<NavTarget>): Boolean =
        true

    override fun createFromState(baseLineState: PromoterModel.State<NavTarget>): PromoterModel.State<NavTarget> =
        baseLineState.copy(
            elements = listOf(element).asElements() + baseLineState.elements,
            activeStartAtIndex = baseLineState.activeStartAtIndex + 1
        )

    override fun createTargetState(fromState: PromoterModel.State<NavTarget>): PromoterModel.State<NavTarget> =
        fromState.copy(
            activeStartAtIndex = fromState.activeStartAtIndex -1
        )
}

fun <NavTarget : Any> Promoter<NavTarget>.addFirst(
    navTarget: NavTarget,
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec
) {
    operation(AddFirst(navTarget), animationSpec)
}
