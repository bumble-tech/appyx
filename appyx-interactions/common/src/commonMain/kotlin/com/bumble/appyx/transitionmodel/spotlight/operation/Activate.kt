package com.bumble.appyx.transitionmodel.spotlight.operation

import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.core.NavElements
import com.bumble.appyx.interactions.core.NavTransition
import com.bumble.appyx.interactions.core.Operation
import com.bumble.appyx.transitionmodel.spotlight.Spotlight
import com.bumble.appyx.transitionmodel.spotlight.Spotlight.State
import com.bumble.appyx.transitionmodel.spotlight.currentIndex

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
