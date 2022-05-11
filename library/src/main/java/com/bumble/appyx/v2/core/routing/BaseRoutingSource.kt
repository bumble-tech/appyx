package com.bumble.appyx.v2.core.routing

import com.bumble.appyx.v2.core.node.ParentNode
import com.bumble.appyx.v2.core.plugin.BackPressHandler
import com.bumble.appyx.v2.core.plugin.Destroyable
import com.bumble.appyx.v2.core.routing.backpresshandlerstrategies.BackPressHandlerStrategy
import com.bumble.appyx.v2.core.routing.backpresshandlerstrategies.DontHandleBackPress
import com.bumble.appyx.v2.core.routing.operationstrategies.ExecuteImmediately
import com.bumble.appyx.v2.core.routing.operationstrategies.OperationStrategy
import com.bumble.appyx.v2.core.state.SavedStateMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.coroutines.EmptyCoroutineContext


/**
 * Base class for routingSource implementations.
 * Use this one if the base behaviour suffice your requirements, you want to use backPressHandler or operationStrategy,
 * and to reduce boilerplate.
 *
 * If more granular configuration is required, you should inherit RoutingSource interface instead.
 */
abstract class BaseRoutingSource<Routing, State>(
    private val backPressHandler: BackPressHandlerStrategy<Routing, State> = DontHandleBackPress(),
    private val operationStrategy: OperationStrategy<Routing, State> = ExecuteImmediately(),
    screenResolver: OnScreenStateResolver<State>,
    protected val scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined),
    private val finalStates: Set<State>,
    private val key: String = ParentNode.KEY_ROUTING_SOURCE,
    savedStateMap: SavedStateMap?
) : RoutingSource<Routing, State>, Destroyable, BackPressHandler by backPressHandler {

    init {
        backPressHandler.init(this, scope)
        operationStrategy.init(this, scope, ::execute)
    }

    constructor(
        backPressHandler: BackPressHandlerStrategy<Routing, State> = DontHandleBackPress(),
        operationStrategy: OperationStrategy<Routing, State> = ExecuteImmediately(),
        screenResolver: OnScreenStateResolver<State>,
        scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined),
        finalState: State?,
        key: String = ParentNode.KEY_ROUTING_SOURCE,
        savedStateMap: SavedStateMap?
    ) : this(
        backPressHandler = backPressHandler,
        operationStrategy = operationStrategy,
        screenResolver = screenResolver,
        scope = scope,
        finalStates = setOfNotNull(finalState),
        key = key,
        savedStateMap = savedStateMap
    )

    protected abstract val initialElements: RoutingElements<Routing, State>

    private val state: MutableStateFlow<RoutingElements<Routing, State>> by lazy {
        MutableStateFlow(savedStateMap?.restoreHistory() ?: initialElements)
    }

    override val elements: StateFlow<RoutingElements<Routing, State>>
        get() = state

    private val onScreenMapper = OnScreenMapper<Routing, State>(scope, screenResolver)

    override val onScreen: StateFlow<RoutingElements<Routing, State>> by lazy(LazyThreadSafetyMode.NONE) {
        onScreenMapper.resolveOnScreenElements(state)
    }

    override val offScreen: StateFlow<RoutingElements<Routing, State>> by lazy(LazyThreadSafetyMode.NONE) {
        onScreenMapper.resolveOffScreenElements(state)
    }

    override val canHandleBackPress: StateFlow<Boolean> by lazy(LazyThreadSafetyMode.NONE) {
        backPressHandler.canHandleBackPress
    }

    override fun accept(operation: Operation<Routing, State>) {
        operationStrategy.accept(operation)
    }

    private fun execute(operation: Operation<Routing, State>) {
        if (operation.isApplicable(state.value)) {
            state.update { operation(it) }
        }
    }

    override fun onTransitionFinished(key: RoutingKey<Routing>) {
        state.update { list ->
            list.mapNotNull {
                if (it.key == key) {
                    it.finishTransitionOrRemove()
                } else {
                    it
                }
            }
        }
    }

    protected open fun RoutingElement<Routing, State>.finishTransitionOrRemove(): RoutingElement<Routing, State>? =
        if (targetState.isFinalState) {
            null
        } else {
            onTransitionFinished()
        }

    override fun saveInstanceState(savedStateMap: MutableMap<String, Any>) {
        savedStateMap[key] =
            elements.value.mapNotNull {
                // Sanitize outputs, removing all transitions
                if (it.targetState.isFinalState) {
                    it.onTransitionFinished()
                } else {
                    null
                }
            }
    }

    override fun destroy() {
        scope.cancel()
    }

    protected val State.isFinalState
        get() = finalStates.contains(this)

    private fun SavedStateMap.restoreHistory() =
        this[key] as? RoutingElements<Routing, State>
}
