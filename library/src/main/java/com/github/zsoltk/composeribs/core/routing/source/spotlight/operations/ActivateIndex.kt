package com.github.zsoltk.composeribs.core.routing.source.spotlight.operations

import android.os.Parcelable
import com.github.zsoltk.composeribs.core.routing.RoutingElements
import com.github.zsoltk.composeribs.core.routing.source.spotlight.Spotlight
import com.github.zsoltk.composeribs.core.routing.source.spotlight.currentIndex
import kotlinx.parcelize.Parcelize

@Parcelize
class ActivateIndex<T : Any>(
    private val index: Int
) : SpotlightOperation<T> {

    override fun isApplicable(elements: RoutingElements<T, Spotlight.TransitionState>) =
        index != elements.currentIndex && index <= elements.lastIndex

    override fun invoke(elements: RoutingElements<T, Spotlight.TransitionState>): RoutingElements<T, Spotlight.TransitionState> {

        val toActivateIndex = this.index
        return elements.mapIndexed { index, element ->
            when {
                index < toActivateIndex -> {
                    element.transitionTo(
                        targetState = Spotlight.TransitionState.INACTIVE_BEFORE,
                        operation = this
                    )
                }
                index == toActivateIndex -> {
                    element.transitionTo(
                        targetState = Spotlight.TransitionState.ACTIVE,
                        operation = this
                    )
                }
                else -> {
                    element.transitionTo(
                        targetState = Spotlight.TransitionState.INACTIVE_AFTER,
                        operation = this
                    )
                }
            }
        }
    }
}

fun <T : Parcelable, K : Parcelable> Spotlight<T, K>.activateIndex(index: Int) {
    accept(ActivateIndex(index))
}
