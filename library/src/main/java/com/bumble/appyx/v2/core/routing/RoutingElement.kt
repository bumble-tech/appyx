package com.bumble.appyx.v2.core.routing

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
class RoutingElement<Routing, State> private constructor(
    val key: @RawValue RoutingKey<Routing>,
    val fromState: @RawValue State,
    val targetState: @RawValue State,
    val operation: @RawValue Operation<Routing, State>,
    val transitionHistory: List<Pair<State, State>>
) : Parcelable {
    constructor(
        key: @RawValue RoutingKey<Routing>,
        fromState: @RawValue State,
        targetState: @RawValue State,
        operation: @RawValue Operation<Routing, State>,
    ) : this(
        key,
        fromState,
        targetState,
        operation,
        if (fromState == targetState) emptyList() else listOf(fromState to targetState)
    )

    fun transitionTo(
        targetState: @RawValue State,
        operation: @RawValue Operation<Routing, State>
    ): RoutingElement<Routing, State> =
        RoutingElement(
            key = key,
            fromState = fromState,
            targetState = targetState,
            operation = operation,
            transitionHistory =
            if (fromState != targetState) {
                transitionHistory + listOf(fromState to targetState)
            } else transitionHistory
        )

    fun onTransitionFinished(): RoutingElement<Routing, State> =
        RoutingElement(
            key = key,
            fromState = targetState,
            targetState = targetState,
            operation = operation,
            transitionHistory = emptyList()
        )


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RoutingElement<*, *>

        if (key != other.key) return false
        if (fromState != other.fromState) return false
        if (targetState != other.targetState) return false
        if (operation != other.operation) return false
        if (transitionHistory != other.transitionHistory) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + (fromState?.hashCode() ?: 0)
        result = 31 * result + (targetState?.hashCode() ?: 0)
        result = 31 * result + operation.hashCode()
        result = 31 * result + transitionHistory.hashCode()
        return result
    }

    override fun toString(): String {
        return "RoutingElement(key=$key, fromState=$fromState, targetState=$targetState, operation=$operation, transitionHistory=$transitionHistory)"
    }
}
