package com.bumble.appyx.navmodel.spotlightadvanced.operation

import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.TransitionState
import kotlinx.parcelize.Parcelize

@Parcelize
class SwitchToCarousel<T : Any>: SpotlightAdvancedOperation<T> {

    override fun isApplicable(elements: NavElements<T, TransitionState>) =
        elements.all { it.fromState !is TransitionState.Carousel }

    override fun invoke(elements: NavElements<T, TransitionState>): NavElements<T, TransitionState> {
        val activeIndex =
            elements.indexOfFirst { it.targetState == TransitionState.Active }

        return elements.mapIndexed { index, element ->
            var targetOffset = index - activeIndex
            if (targetOffset < 0) targetOffset += elements.size
            element.transitionTo(
                newTargetState = TransitionState.Carousel(offset = targetOffset, max = elements.size),
                operation = this
            )
        }
    }
}

fun <T : Any> SpotlightAdvanced<T>.switchToCarousel() {
    accept(SwitchToCarousel())
}
