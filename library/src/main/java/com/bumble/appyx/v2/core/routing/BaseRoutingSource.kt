package com.bumble.appyx.v2.core.routing

import com.bumble.appyx.v2.core.node.ParentNode
import com.bumble.appyx.v2.core.plugin.BackPressHandler
import com.bumble.appyx.v2.core.plugin.Destroyable
import com.bumble.appyx.v2.core.routing.backpresshandlerstrategies.BackPressHandlerStrategy
import com.bumble.appyx.v2.core.routing.backpresshandlerstrategies.DontHandleBackPress
import com.bumble.appyx.v2.core.routing.onscreen.OnScreenMapper
import com.bumble.appyx.v2.core.routing.onscreen.OnScreenStateResolver
import com.bumble.appyx.v2.core.routing.onscreen.isOnScreen
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
    private val screenResolver: OnScreenStateResolver<State>,
    protected val scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined),
    private val finalStates: Set<State>,
    private val key: String = ParentNode.KEY_ROUTING_SOURCE,
    savedStateMap: SavedStateMap?
) : RoutingSource<Routing, State>, Destroyable, BackPressHandler by backPressHandler {

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

    // can't pass it in constructor, it makes harder to restore it from save instance state
    // TODO: think about how we can avoid keeping unnecessary object after state initialization
    protected abstract val initialElements: RoutingElements<Routing, State>

    private val state: MutableStateFlow<RoutingElements<Routing, State>> by lazy {
        MutableStateFlow(savedStateMap?.restoreHistory() ?: initialElements)
    }

    override val elements: StateFlow<RoutingElements<Routing, State>> get() = state

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

    init {
        backPressHandler.init(this, scope)
        operationStrategy.init(this, scope, ::execute)
    }

    override fun accept(operation: Operation<Routing, State>) {
        operationStrategy.accept(operation)
    }

    protected fun updateState(block: (RoutingElements<Routing, State>) -> RoutingElements<Routing, State>) {
        state.update { currentState ->
            val newState = block(currentState)
            sanitizeOffScreenTransitions(newState)
        }
    }

    private fun execute(operation: Operation<Routing, State>) {
        updateState {
            if (operation.isApplicable(it)) {
                operation(it)
            } else {
                it
            }
        }
    }

    /**
     * Off screen transitions can't finish without [onTransitionFinished] callback from UI.
     * In case if we have any, lets finish them instantly.
     */
    protected fun sanitizeOffScreenTransitions(state: RoutingElements<Routing, State>): RoutingElements<Routing, State> =
        state.map {
            if (screenResolver.isOnScreen(it)) {
                it
            } else {
                it.onTransitionFinished()
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
