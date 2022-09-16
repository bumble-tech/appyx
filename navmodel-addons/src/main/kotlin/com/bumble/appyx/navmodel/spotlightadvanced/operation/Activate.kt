package com.bumble.appyx.navmodel.spotlightadvanced.operation

import com.bumble.appyx.core.navigation.RoutingElements
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.TransitionState
import com.bumble.appyx.navmodel.spotlightadvanced.currentIndex
import kotlinx.parcelize.Parcelize

@Parcelize
class Activate<T : Any>(
    private val index: Int
) : SpotlightAdvancedOperation<T> {

    override fun isApplicable(elements: RoutingElements<T, TransitionState>) =
        index != elements.currentIndex && index <= elements.lastIndex && index >= 0

    override fun invoke(elements: RoutingElements<T, TransitionState>): RoutingElements<T, TransitionState> {

        val toActivateIndex = this.index
        return elements.mapIndexed { index, element ->
            when {
                index < toActivateIndex -> {
                    element.transitionTo(
                        newTargetState = TransitionState.InactiveBefore,
                        operation = this
                    )
                }
                index == toActivateIndex -> {
                    element.transitionTo(
                        newTargetState = TransitionState.Active,
                        operation = this
                    )
                }
                else -> {
                    element.transitionTo(
                        newTargetState = TransitionState.InactiveAfter,
                        operation = this
                    )
                }
            }
        }
    }
}

fun <T : Any> SpotlightAdvanced<T>.activate(index: Int) {
    accept(Activate(index))
}
