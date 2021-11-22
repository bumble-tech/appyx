package com.github.zsoltk.composeribs.core.routing.source.backstack

import com.github.zsoltk.composeribs.core.node.ParentNode
import com.github.zsoltk.composeribs.core.routing.OnScreenResolver
import com.github.zsoltk.composeribs.core.plugin.Destroyable
import com.github.zsoltk.composeribs.core.routing.Operation
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.routing.RoutingSourceAdapter
import com.github.zsoltk.composeribs.core.routing.adapter
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState
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
    private val routingSourceResolver: OnScreenResolver<TransitionState> = BackStackOnScreenResolver
) : RoutingSource<T, TransitionState> , Destroyable {

    enum class TransitionState {
        CREATED, ACTIVE, STASHED_IN_BACK_STACK, DESTROYED,
    }

    private val scope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined)

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

    override val adapter: RoutingSourceAdapter<T, TransitionState> by lazy(LazyThreadSafetyMode.NONE) {
        adapter(scope, routingSourceResolver)
    }

    override val elements: StateFlow<BackStackElements<T>> =
        state

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
