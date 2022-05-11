package com.bumble.appyx.v2.core.routing.source.backstack

import com.bumble.appyx.v2.core.node.ParentNode
import com.bumble.appyx.v2.core.routing.BaseRoutingSource
import com.bumble.appyx.v2.core.routing.onscreen.OnScreenStateResolver
import com.bumble.appyx.v2.core.routing.Operation
import com.bumble.appyx.v2.core.routing.RoutingElements
import com.bumble.appyx.v2.core.routing.RoutingKey
import com.bumble.appyx.v2.core.routing.backpresshandlerstrategies.BackPressHandlerStrategy
import com.bumble.appyx.v2.core.routing.operationstrategies.ExecuteImmediately
import com.bumble.appyx.v2.core.routing.operationstrategies.OperationStrategy
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack.TransitionState
import com.bumble.appyx.v2.core.routing.source.backstack.backpresshandler.PopBackPressHandler
import com.bumble.appyx.v2.core.state.SavedStateMap
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class BackStack<Routing : Any>(
    initialElement: Routing,
    savedStateMap: SavedStateMap?,
    private val key: String = ParentNode.KEY_ROUTING_SOURCE,
    backPressHandler: BackPressHandlerStrategy<Routing, TransitionState> = PopBackPressHandler(),
    operationStrategy: OperationStrategy<Routing, TransitionState> = ExecuteImmediately(),
    screenResolver: OnScreenStateResolver<TransitionState> = BackStackOnScreenResolver
) : BaseRoutingSource<Routing, TransitionState>(
    backPressHandler = backPressHandler,
    screenResolver = screenResolver,
    operationStrategy = operationStrategy,
) {

    enum class TransitionState {
        CREATED, ACTIVE, STASHED_IN_BACK_STACK, DESTROYED,
    }

    override val initialState: RoutingElements<Routing, TransitionState> =
        savedStateMap?.restoreHistory() ?: listOf(
            BackStackElement(
                key = RoutingKey(initialElement),
                fromState = TransitionState.ACTIVE,
                targetState = TransitionState.ACTIVE,
                operation = Operation.Noop()
            )
        )

    // TODO consider pulling up
    val routings: StateFlow<List<Routing>> =
        elements
            .map { state -> state.map { routingElement -> routingElement.key.routing } }
            .stateIn(scope, SharingStarted.Eagerly, listOf(initialElement))

    override fun onTransitionFinished(key: RoutingKey<Routing>) {
        updateState { list ->
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

    override fun saveInstanceState(savedStateMap: MutableMap<String, Any>) {
        savedStateMap[key] =
            elements.value.mapNotNull {
                // Sanitize outputs, removing all transitions
                if (it.targetState != TransitionState.DESTROYED) {
                    it.onTransitionFinished()
                } else {
                    null
                }
            }
    }

    private fun SavedStateMap.restoreHistory() =
        this[key] as? BackStackElements<Routing>

}
