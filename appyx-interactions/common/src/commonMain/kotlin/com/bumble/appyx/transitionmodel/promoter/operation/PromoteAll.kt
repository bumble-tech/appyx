package com.bumble.appyx.navmodel.promoter.navmodel2.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.interactions.core.navigation2.NavElements
import com.bumble.appyx.interactions.core.navigation2.NavTransition
import com.bumble.appyx.interactions.core.navigation2.inputsource.AnimatedInputSource
import com.bumble.appyx.interactions.core.navigation2.inputsource.InputSource
import com.bumble.appyx.transitionmodel.promoter.Promoter.State
import com.bumble.appyx.transitionmodel.promoter.PromoterElements
import com.bumble.appyx.transitionmodel.promoter.operation.PromoterOperation
import kotlinx.parcelize.Parcelize

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
