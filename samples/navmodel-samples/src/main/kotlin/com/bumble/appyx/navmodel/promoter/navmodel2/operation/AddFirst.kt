package com.bumble.appyx.navmodel.promoter.navmodel2.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.navmodel.promoter.navmodel2.Promoter
import com.bumble.appyx.navmodel.promoter.navmodel2.Promoter.State.CREATED
import com.bumble.appyx.navmodel.promoter.navmodel2.PromoterElement
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.core.navigation2.NavElements
import com.bumble.appyx.core.navigation2.NavTransition
import com.bumble.appyx.core.navigation2.inputsource.AnimatedInputSource
import com.bumble.appyx.core.navigation2.inputsource.InputSource
import com.bumble.appyx.navmodel.promoter.navmodel2.Promoter.State
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class AddFirst<NavTarget>(
    private val element: @RawValue NavTarget
) : PromoterOperation<NavTarget> {

    override fun isApplicable(elements: NavElements<NavTarget, State>): Boolean =
        true

    override fun invoke(elements: NavElements<NavTarget, State>): NavTransition<NavTarget, State> {
        val new = PromoterElement(
            key = NavKey(element),
            fromState = CREATED,
            targetState = CREATED,
            operation = this
        )

        val newElements = listOf(new) + elements

        return NavTransition(
            fromState = newElements,
            targetState = newElements.transitionTo { it.state.next() },
        )
    }
}

fun <NavTarget : Any> InputSource<NavTarget, State>.addFirst(
    navTarget: NavTarget
) {
    operation(AddFirst(navTarget))
}

fun <NavTarget : Any> AnimatedInputSource<NavTarget, State>.addFirst(
    navTarget: NavTarget,
    animationSpec: AnimationSpec<Float>
) {
    operation(AddFirst(navTarget), animationSpec)
}
