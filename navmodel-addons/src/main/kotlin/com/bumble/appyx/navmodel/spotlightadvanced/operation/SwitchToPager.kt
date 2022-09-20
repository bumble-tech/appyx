package com.bumble.appyx.navmodel.spotlightadvanced.operation

import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.State
import kotlinx.parcelize.Parcelize

@Parcelize
class SwitchToPager<T : Any> : SpotlightAdvancedOperation<T> {

    override fun isApplicable(elements: NavElements<T, State>) =
        elements.all { it.fromState is State.Carousel }

    override fun invoke(elements: NavElements<T, State>): NavElements<T, State> {

        val activeIndex =
            elements.indexOfFirst {
                val state = it.targetState as State.Carousel
                (state.offset % state.max) == 0
            }

        return elements.mapIndexed { index, element ->
            val state = if (index < activeIndex) {
                State.InactiveBefore
            } else if (index == activeIndex) {
                State.Active
            } else {
                State.InactiveAfter
            }
            element.transitionTo(
                newTargetState = state,
                operation = this
            )
        }
    }
}


fun <T : Any> SpotlightAdvanced<T>.switchToPager() {
    accept(SwitchToPager())
}
