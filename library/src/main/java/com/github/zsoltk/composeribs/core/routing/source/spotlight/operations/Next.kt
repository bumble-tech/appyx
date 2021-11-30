package com.github.zsoltk.composeribs.core.routing.source.spotlight.operations

import android.os.Parcelable
import com.github.zsoltk.composeribs.core.routing.RoutingElements
import com.github.zsoltk.composeribs.core.routing.source.spotlight.Spotlight
import com.github.zsoltk.composeribs.core.routing.source.spotlight.Spotlight.TransitionState
import com.github.zsoltk.composeribs.core.routing.source.spotlight.currentIndex
import kotlinx.parcelize.Parcelize

@Parcelize
class Next<T : Any> : SpotlightOperation<T> {

    override fun isApplicable(elements: RoutingElements<T, TransitionState>) =
        elements.lastIndex != elements.currentIndex

    override fun invoke(elements: RoutingElements<T, TransitionState>): RoutingElements<T, TransitionState> {
        val nextKey =
            elements.first { it.fromState == TransitionState.INACTIVE_AFTER }.key

        return elements.map {
            when {
                it.fromState == TransitionState.ACTIVE -> {
                    it.transitionTo(
                        targetState = TransitionState.INACTIVE_BEFORE,
                        operation = this
                    )
                }
                it.key == nextKey -> {
                    it.transitionTo(
                        targetState = TransitionState.ACTIVE,
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

fun <T : Parcelable> Spotlight<T, *>.next() {
    accept(Next())
}


