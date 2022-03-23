package com.bumble.appyx.v2.core.routing.source.spotlight

import com.bumble.appyx.v2.core.node.ParentNode
import com.bumble.appyx.v2.core.routing.BaseRoutingSource
import com.bumble.appyx.v2.core.routing.OnScreenStateResolver
import com.bumble.appyx.v2.core.routing.Operation
import com.bumble.appyx.v2.core.routing.RoutingKey
import com.bumble.appyx.v2.core.routing.backpresshandlerstrategies.BackPressHandlerStrategy
import com.bumble.appyx.v2.core.routing.operationstrategies.ExecuteWithoutStrategy
import com.bumble.appyx.v2.core.routing.operationstrategies.OperationStrategy
import com.bumble.appyx.v2.core.routing.source.spotlight.Spotlight.TransitionState
import com.bumble.appyx.v2.core.routing.source.spotlight.Spotlight.TransitionState.ACTIVE
import com.bumble.appyx.v2.core.routing.source.spotlight.Spotlight.TransitionState.INACTIVE_AFTER
import com.bumble.appyx.v2.core.routing.source.spotlight.Spotlight.TransitionState.INACTIVE_BEFORE
import com.bumble.appyx.v2.core.routing.source.spotlight.backpresshandler.GoToDefault
import com.bumble.appyx.v2.core.state.SavedStateMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class Spotlight<Routing : Any>(
    items: List<Routing>,
    initialActiveItem: Int = 0,
    savedStateMap: SavedStateMap?,
    private val key: String = ParentNode.KEY_ROUTING_SOURCE,
    backPressHandler: BackPressHandlerStrategy<Routing, TransitionState, Spotlight<Routing>> = GoToDefault(
        initialActiveItem
    ),
    operationStrategy: OperationStrategy<Routing, TransitionState> = ExecuteWithoutStrategy(),
    screenResolver: OnScreenStateResolver<TransitionState> = SpotlightOnScreenResolver
) : BaseRoutingSource<Routing, TransitionState, Spotlight<Routing>>(
    backPressHandler = backPressHandler,
    operationStrategy = operationStrategy,
    screenResolver = screenResolver,
) {

    enum class TransitionState {
        INACTIVE_BEFORE, ACTIVE, INACTIVE_AFTER;
    }

    override val state = MutableStateFlow(
        value = savedStateMap?.restoreHistory() ?: items.toSpotlightElements(initialActiveItem)
    )

    override fun onTransitionFinished(key: RoutingKey<Routing>) {
        state.update { elements ->
            elements.map {
                if (key == it.key) {
                    it.onTransitionFinished()
                } else {
                    it
                }
            }
        }
    }

    override fun saveInstanceState(savedStateMap: MutableMap<String, Any>) {
        savedStateMap[key] =
            state.value.map {
                if (it.targetState != it.fromState) {
                    it.onTransitionFinished()
                }
                it
            }
    }

    private fun SavedStateMap.restoreHistory() =
        this[key] as? SpotlightElements<Routing>

    private fun List<Routing>.toSpotlightElements(activeIndex: Int): SpotlightElements<Routing> =
        mapIndexed { index, item ->
            val state = when {
                index < activeIndex -> INACTIVE_BEFORE
                index == activeIndex -> ACTIVE
                else -> INACTIVE_AFTER
            }
            SpotlightElement(
                key = RoutingKey(item),
                fromState = state,
                targetState = state,
                operation = Operation.Noop()
            )
        }
}
