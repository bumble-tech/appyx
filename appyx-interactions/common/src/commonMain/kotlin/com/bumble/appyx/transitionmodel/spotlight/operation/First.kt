package com.bumble.appyx.transitionmodel.spotlight.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.core.NavElements
import com.bumble.appyx.interactions.core.NavTransition
import com.bumble.appyx.interactions.core.Operation
import com.bumble.appyx.interactions.core.inputsource.AnimatedInputSource
import com.bumble.appyx.interactions.core.inputsource.InputSource
import com.bumble.appyx.transitionmodel.spotlight.Spotlight.State
import com.bumble.appyx.transitionmodel.spotlight.Spotlight.State.ACTIVE
import com.bumble.appyx.transitionmodel.spotlight.Spotlight.State.INACTIVE_AFTER
import com.bumble.appyx.transitionmodel.spotlight.Spotlight.State.INACTIVE_BEFORE


@Parcelize
class First<NavTarget : Any> : Operation<NavTarget, State> {

    override fun isApplicable(elements: NavElements<NavTarget, State>) =
        elements.any { it.state == INACTIVE_BEFORE }

    override fun invoke(elements: NavElements<NavTarget, State>): NavTransition<NavTarget, State> {
        return NavTransition(
            fromState = elements,
            targetState = elements.mapIndexed { index, element ->
                element.transitionTo(
                    newTargetState = when (index) {
                        0 -> ACTIVE
                        else -> INACTIVE_AFTER
                    },
                    operation = this
                )
            }
        )
    }
}

fun <NavTarget : Any> InputSource<NavTarget, State>.first() {
    operation(First())
}

fun <NavTarget : Any> AnimatedInputSource<NavTarget, State>.first(animationSpec: AnimationSpec<Float>) {
    operation(First(), animationSpec)
}



