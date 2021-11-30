package com.github.zsoltk.composeribs.core.routing.source.spotlight.operations

import android.os.Parcelable
import com.github.zsoltk.composeribs.core.routing.RoutingElements
import com.github.zsoltk.composeribs.core.routing.source.spotlight.Spotlight
import com.github.zsoltk.composeribs.core.routing.source.spotlight.currentIndex
import kotlinx.parcelize.Parcelize


@Parcelize
class Previous<T : Any> : SpotlightOperation<T> {

    override fun isApplicable(elements: RoutingElements<T, Spotlight.TransitionState>) =
        elements.currentIndex != 0

    override fun invoke(elements: RoutingElements<T, Spotlight.TransitionState>): RoutingElements<T, Spotlight.TransitionState> {
        val previousKey =
            elements.last { it.fromState == Spotlight.TransitionState.INACTIVE_BEFORE }.key

        return elements.map {
            when {
                it.fromState == Spotlight.TransitionState.ACTIVE -> {
                    it.transitionTo(
                        targetState = Spotlight.TransitionState.INACTIVE_AFTER,
                        operation = this
                    )
                }
                it.key == previousKey -> {
                    it.transitionTo(
                        targetState = Spotlight.TransitionState.ACTIVE,
                        operation = this
                    )
                }
                else -> {
                    it
                }
            }
        }
    }
}

fun <T : Parcelable> Spotlight<T, *>.previous() {
    accept(Previous())
}


