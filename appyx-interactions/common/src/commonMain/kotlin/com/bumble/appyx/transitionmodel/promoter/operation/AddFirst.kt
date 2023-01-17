package com.bumble.appyx.transitionmodel.promoter.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.interactions.core.NavElements
import com.bumble.appyx.interactions.core.NavKey
import com.bumble.appyx.interactions.core.NavTransition
import com.bumble.appyx.interactions.core.inputsource.AnimatedInputSource
import com.bumble.appyx.interactions.core.inputsource.InputSource
import com.bumble.appyx.transitionmodel.promoter.Promoter.State.CREATED
import com.bumble.appyx.transitionmodel.promoter.PromoterElement
import com.bumble.appyx.transitionmodel.promoter.Promoter.State
import com.bumble.appyx.interactions.Parcelize

@Parcelize
data class AddFirst<NavTarget>(
    private val element: NavTarget // FIXME @RawValue
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
