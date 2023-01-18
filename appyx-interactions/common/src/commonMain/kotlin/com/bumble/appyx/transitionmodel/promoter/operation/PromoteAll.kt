package com.bumble.appyx.transitionmodel.promoter.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.core.NavElements
import com.bumble.appyx.interactions.core.NavTransition
import com.bumble.appyx.transitionmodel.promoter.Promoter
import com.bumble.appyx.transitionmodel.promoter.PromoterElements
import com.bumble.appyx.transitionmodel.promoter.PromoterModel.State

@Parcelize
class PromoteAll<NavTarget> : PromoterOperation<NavTarget> {

    override fun isApplicable(elements: PromoterElements<NavTarget>): Boolean =
        true

    override fun invoke(elements: NavElements<NavTarget, State>): NavTransition<NavTarget, State> {
        return NavTransition(
            fromState = elements,
            targetState = elements.transitionTo { it.state.next() }
        )
    }
}

fun <NavTarget : Any> Promoter<NavTarget>.promoteAll(
    animationSpec: AnimationSpec<Float>
) {
    operation(PromoteAll(), animationSpec)
}
