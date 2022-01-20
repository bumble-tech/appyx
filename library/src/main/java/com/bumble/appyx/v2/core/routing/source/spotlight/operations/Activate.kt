package com.bumble.appyx.v2.core.routing.source.spotlight.operations

import android.os.Parcelable
import com.bumble.appyx.v2.core.routing.RoutingElements
import com.bumble.appyx.v2.core.routing.source.spotlight.Spotlight
import com.bumble.appyx.v2.core.routing.source.spotlight.Spotlight.TransitionState
import com.bumble.appyx.v2.core.routing.source.spotlight.current
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
class Activate<T : Any, K>(
    private val poolKey: @RawValue K
) : SpotlightOperation<T> {

    private val keyId = poolKey.toString()

    override fun isApplicable(elements: RoutingElements<T, TransitionState>) =
        keyId != elements.current?.key?.id && elements.any { it.key.id == keyId }

    override fun invoke(elements: RoutingElements<T, TransitionState>): RoutingElements<T, TransitionState> {

        val toActivateIndex = elements.indexOfFirst { it.key.id == keyId }
        return elements.mapIndexed { index, element ->
            when {
                index < toActivateIndex -> {
                    element.transitionTo(
                        targetState = TransitionState.INACTIVE_BEFORE,
                        operation = this
                    )
                }
                index == toActivateIndex -> {
                    element.transitionTo(
                        targetState = TransitionState.ACTIVE,
                        operation = this
                    )
                }
                else -> {
                    element.transitionTo(
                        targetState = TransitionState.INACTIVE_AFTER,
                        operation = this
                    )
                }
            }
        }
    }
}

fun <T : Parcelable, K : Parcelable> Spotlight<T, K>.activate(key: K) {
    accept(Activate(key))
}
