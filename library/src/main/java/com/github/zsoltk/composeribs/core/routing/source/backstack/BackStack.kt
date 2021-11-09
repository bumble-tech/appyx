package com.github.zsoltk.composeribs.core.routing.source.backstack

import android.os.Parcelable
import com.github.zsoltk.composeribs.core.ParentNode
import com.github.zsoltk.composeribs.core.SavedStateMap
import com.github.zsoltk.composeribs.core.routing.Operation
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.routing.RoutingState
import com.github.zsoltk.composeribs.core.routing.UuidGenerator
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.pop
import com.github.zsoltk.composeribs.core.unsuspendedMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

class BackStack<T : Any>(
    initialElement: T,
    savedStateMap: SavedStateMap?,
    private val key: String = ParentNode.KEY_ROUTING_SOURCE,
) : RoutingSource<T, BackStack.TransitionState> {

    @Parcelize
    data class LocalRoutingKey<T>(
        override val routing: @RawValue T,
        val uuid: Int,
    ) : RoutingKey<T>, Parcelable

    enum class TransitionState {
        CREATED, ON_SCREEN, STASHED_IN_BACK_STACK, DESTROYED,
    }

    // TODO Replace with UUID for restoration simplicity?
    private val tmpCounter = UuidGenerator(
        savedStateMap
            ?.restoreHistory()
            ?.elements
            ?.maxOf { (it.key as LocalRoutingKey<*>).uuid }
            ?: 0
    )

    private val state = MutableStateFlow(
        value = savedStateMap?.restoreHistory() ?: RoutingState(
            elements = listOf(
                BackStackElement(
                    key = LocalRoutingKey(initialElement, tmpCounter.incrementAndGet()),
                    fromState = TransitionState.ON_SCREEN,
                    targetState = TransitionState.ON_SCREEN,
                    operation = Operation.Noop()
                )
            ),
            operation = Operation.Noop()
        )
    )

    override val all: StateFlow<RoutingState<T, TransitionState>> =
        state

    override val offScreen: StateFlow<RoutingState<T, TransitionState>> =
        state.unsuspendedMap { state -> state.copy(elements = state.elements.filter { !it.isOnScreen() }) }

    override val onScreen: StateFlow<RoutingState<T, TransitionState>> =
        state.unsuspendedMap { state -> state.copy(elements = state.elements.filter { it.isOnScreen() }) }

    override val canHandleBackPress: StateFlow<Boolean> =
        state.unsuspendedMap { state -> state.elements.count { it.targetState == TransitionState.STASHED_IN_BACK_STACK } > 0 }

    override fun onTransitionFinished(key: RoutingKey<T>) {
        state.update { state ->
            state.copy(
                elements = state.elements.mapNotNull {
                    if (it.key == key) {
                        when (it.targetState) {
                            TransitionState.DESTROYED ->
                                null
                            TransitionState.STASHED_IN_BACK_STACK ->
                                it.copy(fromState = it.targetState)
                            TransitionState.CREATED,
                            TransitionState.ON_SCREEN ->
                                it.copy(fromState = it.targetState)
                        }
                    } else {
                        it
                    }
                }
            )
        }
    }

    fun perform(operation: BackStackOperation<T>) {
        val elements = state.value.elements
        if (operation.isApplicable(elements)) {
            state.update {
                RoutingState(
                    elements = operation(elements, tmpCounter),
                    operation = operation
                )
            }
        }
    }

    override fun onBackPressed() {
        pop()
    }

    override fun saveInstanceState(savedStateMap: MutableMap<String, Any>) {
        savedStateMap[key] =
            state.value.copy(
                elements = state.value.elements.mapNotNull {
                    // Sanitize outputs, removing all transitions
                    if (it.targetState != TransitionState.DESTROYED) {
                        it.copy(fromState = it.targetState)
                    } else {
                        null
                    }
                }
            )
    }

    private fun SavedStateMap.restoreHistory() =
        this[key] as? RoutingState<T, TransitionState>

    private fun BackStackElement<T>.isOnScreen(): Boolean =
        when (targetState) {
            TransitionState.CREATED ->
                error("Impossible target state")
            TransitionState.ON_SCREEN ->
                true
            TransitionState.STASHED_IN_BACK_STACK,
            TransitionState.DESTROYED ->
                fromState == TransitionState.ON_SCREEN
        }

    override fun isOnScreen(key: RoutingKey<T>): Boolean =
        state.value.elements.find { it.key == key }?.isOnScreen() ?: false
}
