package com.bumble.appyx.transitionmodel.promoter.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.interactions.core.NavElements
import com.bumble.appyx.interactions.core.NavTransition
import com.bumble.appyx.interactions.core.inputsource.AnimatedInputSource
import com.bumble.appyx.interactions.core.inputsource.InputSource
import com.bumble.appyx.transitionmodel.promoter.Promoter.State
import com.bumble.appyx.transitionmodel.promoter.PromoterElements
import com.bumble.appyx.interactions.Parcelize

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


fun <NavTarget : Any> InputSource<NavTarget, State>.promoteAll() {
    operation(PromoteAll())
}

fun <NavTarget : Any> AnimatedInputSource<NavTarget, State>.promoteAll(
    animationSpec: AnimationSpec<Float>
) {
    operation(PromoteAll(), animationSpec)
}
