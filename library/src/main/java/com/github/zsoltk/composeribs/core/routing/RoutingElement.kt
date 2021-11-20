package com.github.zsoltk.composeribs.core.routing

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
class RoutingElement<Key, State>(
    val key: @RawValue RoutingKey<Key>,
    val fromState: @RawValue State,
    val targetState: @RawValue State,
    val operation: @RawValue Operation<Key, out State> = Operation.Noop(),
    val transitionHistory: MutableList<Pair<State, State>> = mutableListOf()
) : Parcelable {

    init {
        if (fromState != targetState) {
            transitionHistory.add(fromState to targetState)
        }
    }

    fun transitionTo(
        targetState: @RawValue State,
        operation: @RawValue Operation<Key, out State>
    ): RoutingElement<Key, State> {
        if (this.fromState != targetState) {
            transitionHistory.add(this.fromState to targetState)
        }
        return RoutingElement(
            key = key,
            fromState = fromState,
            targetState = targetState,
            operation = operation,
            transitionHistory = transitionHistory
        )
    }

    fun onTransitionFinished(): RoutingElement<Key, State> {
        return RoutingElement(
            key = key,
            fromState = targetState,
            targetState = targetState,
            operation = operation,
            transitionHistory = mutableListOf()
        )
    }

}
