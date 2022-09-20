package com.bumble.appyx.navmodel.spotlight.operation

import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.navmodel.spotlight.Spotlight
import com.bumble.appyx.navmodel.spotlight.Spotlight.TransitionState
import com.bumble.appyx.navmodel.spotlight.currentIndex
import kotlinx.parcelize.Parcelize

@Parcelize
class Activate<T : Any>(
    private val index: Int
) : SpotlightOperation<T> {

    override fun isApplicable(elements: NavElements<T, TransitionState>) =
        index != elements.currentIndex && index <= elements.lastIndex && index >= 0

    override fun invoke(elements: NavElements<T, TransitionState>): NavElements<T, TransitionState> {

        val toActivateIndex = this.index
        return elements.mapIndexed { index, element ->
            when {
                index < toActivateIndex -> {
                    element.transitionTo(
                        newTargetState = TransitionState.INACTIVE_BEFORE,
                        operation = this
                    )
                }
                index == toActivateIndex -> {
                    element.transitionTo(
                        newTargetState = TransitionState.ACTIVE,
                        operation = this
                    )
                }
                else -> {
                    element.transitionTo(
                        newTargetState = TransitionState.INACTIVE_AFTER,
                        operation = this
                    )
                }
            }
        }
    }
}


fun <T : Any> Spotlight<T>.activate(index: Int) {
    accept(Activate(index))
}
