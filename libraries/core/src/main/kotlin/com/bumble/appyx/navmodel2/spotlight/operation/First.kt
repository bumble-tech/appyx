package com.bumble.appyx.navmodel2.spotlight.operation

import com.bumble.appyx.core.navigation2.NavElements
import com.bumble.appyx.core.navigation2.NavTransition
import com.bumble.appyx.core.navigation2.Operation
import com.bumble.appyx.core.navigation2.inputsource.InputSource
import com.bumble.appyx.navmodel2.spotlight.Spotlight.State
import com.bumble.appyx.navmodel2.spotlight.Spotlight.State.ACTIVE
import com.bumble.appyx.navmodel2.spotlight.Spotlight.State.INACTIVE_AFTER
import com.bumble.appyx.navmodel2.spotlight.Spotlight.State.INACTIVE_BEFORE
import kotlinx.parcelize.Parcelize


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



