package com.bumble.appyx.navmodel2.spotlight.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.core.navigation2.NavElements
import com.bumble.appyx.core.navigation2.NavTransition
import com.bumble.appyx.core.navigation2.Operation
import com.bumble.appyx.core.navigation2.inputsource.AnimatedInputSource
import com.bumble.appyx.core.navigation2.inputsource.InputSource
import com.bumble.appyx.navmodel2.spotlight.Spotlight.State
import com.bumble.appyx.navmodel2.spotlight.Spotlight.State.ACTIVE
import com.bumble.appyx.navmodel2.spotlight.Spotlight.State.INACTIVE_AFTER
import com.bumble.appyx.navmodel2.spotlight.Spotlight.State.INACTIVE_BEFORE
import kotlinx.parcelize.Parcelize


@Parcelize
class Last<NavTarget : Any> : Operation<NavTarget, State> {

    override fun isApplicable(elements: NavElements<NavTarget, State>) =
        elements.any { it.state == INACTIVE_AFTER }

    override fun invoke(elements: NavElements<NavTarget, State>): NavTransition<NavTarget, State> {
        return NavTransition(
            fromState = elements,
            targetState = elements.mapIndexed { index, element ->
                element.transitionTo(
                    newTargetState = when (index) {
                        elements.lastIndex -> ACTIVE
                        else -> INACTIVE_BEFORE
                    },
                    operation = this
                )
            }
        )
    }
}

fun <NavTarget : Any> InputSource<NavTarget, State>.last() {
    operation(Last())
}

fun <NavTarget : Any> AnimatedInputSource<NavTarget, State>.last(animationSpec: AnimationSpec<Float>) {
    operation(Last(), animationSpec)
}

