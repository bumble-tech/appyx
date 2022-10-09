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
class Next<NavTarget : Any> : Operation<NavTarget, State> {

    override fun isApplicable(elements: NavElements<NavTarget, State>) =
        elements.any { it.fromState == INACTIVE_AFTER && it.state == INACTIVE_AFTER }

    override fun invoke(elements: NavElements<NavTarget, State>): NavTransition<NavTarget, State> {
        val nextKey = elements.first { it.state == INACTIVE_AFTER }.key

        val targetState = elements.map {
            when {
                it.state == ACTIVE -> {
                    it.transitionTo(
                        newTargetState = INACTIVE_BEFORE,
                        operation = this
                    )
                }
                it.key == nextKey -> {
                    it.transitionTo(
                        newTargetState = ACTIVE,
                        operation = this
                    )
                }
                else -> {
                    it
                }
            }
        }

        return NavTransition(
            fromState = elements,
            targetState = targetState
        )
    }
}

fun <NavTarget : Any> InputSource<NavTarget, State>.next() {
    operation(Next())
}


