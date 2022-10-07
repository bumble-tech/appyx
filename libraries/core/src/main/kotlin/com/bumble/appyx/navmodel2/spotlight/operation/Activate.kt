package com.bumble.appyx.navmodel2.spotlight.operation

import com.bumble.appyx.core.navigation2.NavElements
import com.bumble.appyx.core.navigation2.NavTransition
import com.bumble.appyx.core.navigation2.Operation
import com.bumble.appyx.navmodel2.spotlight.Spotlight
import com.bumble.appyx.navmodel2.spotlight.Spotlight.State
import com.bumble.appyx.navmodel2.spotlight.currentIndex
import kotlinx.parcelize.Parcelize

@Parcelize
class Activate<NavTarget : Any>(private val index: Int) : Operation<NavTarget, State> {

    override fun isApplicable(elements: NavElements<NavTarget, State>) =
        index != elements.currentIndex && index <= elements.lastIndex && index >= 0

    override fun invoke(elements: NavElements<NavTarget, State>): NavTransition<NavTarget, State> {
        val toActivateIndex = this.index
        val targetState = elements.mapIndexed { index, element ->
            when {
                index < toActivateIndex -> {
                    element.transitionTo(
                        newTargetState = State.INACTIVE_BEFORE,
                        operation = this
                    )
                }
                index == toActivateIndex -> {
                    element.transitionTo(
                        newTargetState = State.ACTIVE,
                        operation = this
                    )
                }
                else -> {
                    element.transitionTo(
                        newTargetState = State.INACTIVE_AFTER,
                        operation = this
                    )
                }
            }
        }

        return NavTransition(
            fromState = elements,
            targetState = targetState
        )
    }
}


fun <T : Any> Spotlight<T>.activate(index: Int) {
    enqueue(Activate(index))
}
