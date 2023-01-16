package com.bumble.appyx.interactions.core

import androidx.compose.runtime.Immutable

// FIXME @Parcelize
@Immutable
class NavElement<NavTarget, State> private constructor(
    val key: NavKey<NavTarget>, // FIXME @RawValue
    val fromState: State, // FIXME @RawValue
    val state: State, // FIXME @RawValue
    val operation: Operation<NavTarget, State>, // FIXME @RawValue
    val transitionHistory: List<Pair<State, State>>
) { // FIXME : Parcelable {
    constructor(
        key: NavKey<NavTarget>, // FIXME @RawValue
        fromState: State, // FIXME @RawValue
        targetState: State, // FIXME @RawValue
        operation: Operation<NavTarget, State>, // FIXME @RawValue
    ) : this(
        key,
        fromState,
        targetState,
        operation,
        if (fromState == targetState) emptyList() else listOf(fromState to targetState)
    )

    fun transitionTo(
        newTargetState: State, // FIXME @RawValue
        operation: Operation<NavTarget, State> // FIXME @RawValue
    ): NavElement<NavTarget, State> =
        NavElement(
            key = key,
            fromState = fromState,
            state = newTargetState,
            operation = operation,
            transitionHistory =
            if (fromState != newTargetState) {
                transitionHistory + listOf(fromState to newTargetState)
            } else transitionHistory
        )

    fun onTransitionFinished(): NavElement<NavTarget, State> =
        NavElement(
            key = key,
            fromState = state,
            state = state,
            operation = operation,
            transitionHistory = emptyList()
        )


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NavElement<*, *>

        if (key != other.key) return false
        if (fromState != other.fromState) return false
        if (state != other.state) return false
        if (operation != other.operation) return false
        if (transitionHistory != other.transitionHistory) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + (fromState?.hashCode() ?: 0)
        result = 31 * result + (state?.hashCode() ?: 0)
        result = 31 * result + operation.hashCode()
        result = 31 * result + transitionHistory.hashCode()
        return result
    }

    override fun toString(): String {
        return "NavElement(key=$key, fromState=$fromState, targetState=$state, " +
            "operation=$operation, transitionHistory=$transitionHistory)"
    }
}
