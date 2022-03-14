package com.bumble.appyx.v2.core.routing.source.backstack

import com.bumble.appyx.v2.core.node.ParentNode
import com.bumble.appyx.v2.core.plugin.Destroyable
import com.bumble.appyx.v2.core.routing.BaseRoutingSource
import com.bumble.appyx.v2.core.routing.OnScreenStateResolver
import com.bumble.appyx.v2.core.routing.Operation
import com.bumble.appyx.v2.core.routing.RoutingKey
import com.bumble.appyx.v2.core.routing.RoutingPlugin
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack.*
import com.bumble.appyx.v2.core.routing.source.backstack.backpressHandler.PopBackPressHandler
import com.bumble.appyx.v2.core.state.SavedStateMap
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class BackStack<T : Any>(
    initialElement: T,
    savedStateMap: SavedStateMap?,
    private val key: String = ParentNode.KEY_ROUTING_SOURCE,
    plugins: List<RoutingPlugin<T, TransitionState>> = listOf(PopBackPressHandler()),
    screenResolver: OnScreenStateResolver<TransitionState> = BackStackOnScreenResolver
) : BaseRoutingSource<T, TransitionState>(plugins, screenResolver), Destroyable {

    enum class TransitionState {
        CREATED, ACTIVE, STASHED_IN_BACK_STACK, DESTROYED,
    }

    override val state = MutableStateFlow(
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
