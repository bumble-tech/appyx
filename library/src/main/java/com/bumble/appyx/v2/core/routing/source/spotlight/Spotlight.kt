package com.bumble.appyx.v2.core.routing.source.spotlight

import com.bumble.appyx.v2.core.node.ParentNode
import com.bumble.appyx.v2.core.routing.BackPressHandler
import com.bumble.appyx.v2.core.routing.BaseRoutingSource
import com.bumble.appyx.v2.core.routing.OnScreenStateResolver
import com.bumble.appyx.v2.core.routing.Operation
import com.bumble.appyx.v2.core.routing.OperationStrategy
import com.bumble.appyx.v2.core.routing.RoutingKey
import com.bumble.appyx.v2.core.routing.source.spotlight.Spotlight.TransitionState
import com.bumble.appyx.v2.core.routing.source.spotlight.Spotlight.TransitionState.ACTIVE
import com.bumble.appyx.v2.core.routing.source.spotlight.Spotlight.TransitionState.INACTIVE_AFTER
import com.bumble.appyx.v2.core.routing.source.spotlight.Spotlight.TransitionState.INACTIVE_BEFORE
import com.bumble.appyx.v2.core.routing.source.spotlight.backpresshandler.GoToDefault
import com.bumble.appyx.v2.core.routing.source.spotlight.backpresshandler.GoToPrevious
import com.bumble.appyx.v2.core.state.SavedStateMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class Spotlight<T : Any>(
    items: List<T>,
    initialActiveItem: Int = 0,
    savedStateMap: SavedStateMap?,
    private val key: String = ParentNode.KEY_ROUTING_SOURCE,
    backPressHandler: BackPressHandler<T, TransitionState> = GoToDefault(initialActiveItem),
    operationStrategy: OperationStrategy<T, TransitionState>? = null,
    screenResolver: OnScreenStateResolver<TransitionState> = SpotlightOnScreenResolver
) : BaseRoutingSource<T, TransitionState>(
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

    override val elements: StateFlow<SpotlightElements<T>> =
        state

    override fun onTransitionFinished(key: RoutingKey<T>) {
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
        this[key] as? SpotlightElements<T>

    private fun List<T>.toSpotlightElements(activeIndex: Int): SpotlightElements<T> =
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
