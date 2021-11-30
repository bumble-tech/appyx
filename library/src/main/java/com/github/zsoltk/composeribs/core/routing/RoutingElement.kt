package com.github.zsoltk.composeribs.core.routing

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class RoutingElement<Key, State> internal constructor(
    val key: @RawValue RoutingKey<Key>,
    val fromState: @RawValue State,
    val targetState: @RawValue State,
    val operation: @RawValue Operation<Key, out State>,
    private val _transitionHistory: MutableList<Pair<State, State>>
) : Parcelable {

    @IgnoredOnParcel
    val transitionHistory: List<Pair<State, State>> = _transitionHistory

    constructor(
        key: @RawValue RoutingKey<Key>,
        fromState: @RawValue State,
        targetState: @RawValue State,
        operation: @RawValue Operation<Key, out State>,
    ) : this(key, fromState, targetState, operation, mutableListOf(fromState to targetState))

    fun transitionTo(
        targetState: @RawValue State,
        operation: @RawValue Operation<Key, out State>
    ): RoutingElement<Key, State> {
        if (this.fromState != targetState) {
            _transitionHistory.add(this.fromState to targetState)
        }
        return RoutingElement(
            key = key,
            fromState = fromState,
            targetState = targetState,
            operation = operation,
            _transitionHistory = _transitionHistory
        )
    }

    fun onTransitionFinished(): RoutingElement<Key, State> {
        return RoutingElement(
            key = key,
            fromState = targetState,
            targetState = targetState,
            operation = operation,
            _transitionHistory = mutableListOf()
        )
    }
}
