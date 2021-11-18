package com.github.zsoltk.composeribs.core.routing.source.backstack

import com.github.zsoltk.composeribs.core.node.ParentNode
import com.github.zsoltk.composeribs.core.plugin.Destroyable
import com.github.zsoltk.composeribs.core.routing.Operation
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.pop
import com.github.zsoltk.composeribs.core.state.SavedStateMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlin.coroutines.EmptyCoroutineContext

class BackStack<T : Any>(
    initialElement: T,
    savedStateMap: SavedStateMap?,
    private val key: String = ParentNode.KEY_ROUTING_SOURCE,
) : RoutingSource<T, BackStack.TransitionState>, Destroyable {

    enum class TransitionState {
        CREATED, ON_SCREEN, STASHED_IN_BACK_STACK, DESTROYED,
    }

    private val scope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined)

    private val state = MutableStateFlow(
        value = savedStateMap?.restoreHistory() ?: listOf(
            BackStackElement(
                key = RoutingKey(initialElement),
                fromState = TransitionState.ON_SCREEN,
                targetState = TransitionState.ON_SCREEN,
                operation = Operation.Noop()
            )
        )
    )

    override val all: StateFlow<BackStackElements<T>> =
        state

    override val offScreen: StateFlow<BackStackElements<T>> =
        state.map { list -> list.filter { !it.isOnScreen() } }
            .stateIn(scope, SharingStarted.Eagerly, emptyList())

    override val onScreen: StateFlow<BackStackElements<T>> =
        state.map { list -> list.filter { it.isOnScreen() } }
            .stateIn(scope, SharingStarted.Eagerly, emptyList())

    override val canHandleBackPress: StateFlow<Boolean> =
        state.map { list -> list.count { it.targetState == TransitionState.STASHED_IN_BACK_STACK } > 0 }
            .stateIn(scope, SharingStarted.Eagerly, false)

    override fun onTransitionFinished(key: RoutingKey<T>) {
        state.update { list ->
            list.mapNotNull {
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
        }
    }

    fun perform(operation: BackStackOperation<T>) {
        if (operation.isApplicable(state.value)) {
            state.update { operation(it) }
        }
    }

    override fun onBackPressed() {
        pop()
    }

    override fun saveInstanceState(savedStateMap: MutableMap<String, Any>) {
        savedStateMap[key] =
            state.value.mapNotNull {
                // Sanitize outputs, removing all transitions
                if (it.targetState != TransitionState.DESTROYED) {
                    it.copy(fromState = it.targetState)
                } else {
                    null
                }
            }
    }

    private fun SavedStateMap.restoreHistory() =
        this[key] as? BackStackElements<T>

    private fun BackStackElement<T>.isOnScreen(): Boolean =
        if (targetState != fromState) {
            true
        } else {
            fromState == TransitionState.ON_SCREEN || fromState == TransitionState.CREATED
        }

    override fun isOnScreen(key: RoutingKey<T>): Boolean =
        state.value.find { it.key == key }?.isOnScreen() ?: false

    override fun destroy() {
        scope.cancel()
    }

}
