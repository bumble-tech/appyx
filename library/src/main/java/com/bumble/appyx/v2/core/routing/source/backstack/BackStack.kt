package com.bumble.appyx.v2.core.routing.source.backstack

import com.bumble.appyx.v2.core.node.ParentNode
import com.bumble.appyx.v2.core.plugin.Destroyable
import com.bumble.appyx.v2.core.routing.OnScreenMapper
import com.bumble.appyx.v2.core.routing.Operation
import com.bumble.appyx.v2.core.routing.RoutingKey
import com.bumble.appyx.v2.core.routing.RoutingSource
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack.TransitionState
import com.bumble.appyx.v2.core.routing.source.backstack.operation.pop
import com.bumble.appyx.v2.core.state.SavedStateMap
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
    allowBackPressHandling: Boolean = true,
) : RoutingSource<T, TransitionState>, Destroyable {

    enum class TransitionState {
        CREATED, ACTIVE, STASHED_IN_BACK_STACK, DESTROYED,
    }

    private val scope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined)

    private val onScreenMapper =
        OnScreenMapper<T, TransitionState>(scope, BackStackOnScreenResolver)

    private val state = MutableStateFlow(
        value = savedStateMap?.restoreHistory() ?: listOf(
            BackStackElement(
                key = RoutingKey(initialElement),
                fromState = TransitionState.ACTIVE,
                targetState = TransitionState.ACTIVE,
                operation = Operation.Noop()
            )
        )
    )

    override val elements: StateFlow<BackStackElements<T>> =
        state

    // TODO consider pulling up
    val routings: StateFlow<List<T>> =
        state
            .map { state -> state.map { routingElement -> routingElement.key.routing } }
            .stateIn(scope, SharingStarted.Eagerly, listOf(initialElement))

    override val onScreen: StateFlow<BackStackElements<T>> =
        onScreenMapper.resolveOnScreenElements(state)

    override val offScreen: StateFlow<BackStackElements<T>> =
        onScreenMapper.resolveOffScreenElements(state)

    override val canHandleBackPress: StateFlow<Boolean> =
        if (allowBackPressHandling) state.map { list -> list.count { it.targetState == TransitionState.STASHED_IN_BACK_STACK } > 0 }
            .stateIn(scope, SharingStarted.Eagerly, false)
        else MutableStateFlow(false)

    override fun onTransitionFinished(key: RoutingKey<T>) {
        state.update { list ->
            list.mapNotNull {
                if (it.key == key) {
                    when (it.targetState) {
                        TransitionState.DESTROYED ->
                            null
                        TransitionState.STASHED_IN_BACK_STACK,
                        TransitionState.CREATED,
                        TransitionState.ACTIVE ->
                            it.onTransitionFinished()
                    }
                } else {
                    it
                }
            }
        }
    }

    override fun accept(operation: Operation<T, TransitionState>) {
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
                    it.onTransitionFinished()
                } else {
                    null
                }
            }
    }

    private fun SavedStateMap.restoreHistory() =
        this[key] as? BackStackElements<T>


    override fun destroy() {
        scope.cancel()
    }
}
