package com.bumble.appyx.transitionmodel.promoter.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.RawValue
import com.bumble.appyx.interactions.core.NavElements
import com.bumble.appyx.interactions.core.NavKey
import com.bumble.appyx.interactions.core.NavTransition
import com.bumble.appyx.transitionmodel.promoter.Promoter
import com.bumble.appyx.transitionmodel.promoter.PromoterElement
import com.bumble.appyx.transitionmodel.promoter.PromoterModel.State
import com.bumble.appyx.transitionmodel.promoter.PromoterModel.State.CREATED

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

fun <NavTarget : Any> Promoter<NavTarget>.addFirst(
    navTarget: NavTarget,
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec
) {
    operation(AddFirst(navTarget), animationSpec)
}
