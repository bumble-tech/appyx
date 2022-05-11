package com.bumble.appyx.v2.core.routing

import com.bumble.appyx.v2.core.plugin.BackPressHandler
import com.bumble.appyx.v2.core.plugin.Destroyable
import com.bumble.appyx.v2.core.routing.backpresshandlerstrategies.BackPressHandlerStrategy
import com.bumble.appyx.v2.core.routing.backpresshandlerstrategies.DontHandleBackPress
import com.bumble.appyx.v2.core.routing.onscreen.OnScreenMapper
import com.bumble.appyx.v2.core.routing.onscreen.OnScreenStateResolver
import com.bumble.appyx.v2.core.routing.onscreen.isOnScreen
import com.bumble.appyx.v2.core.routing.operationstrategies.ExecuteImmediately
import com.bumble.appyx.v2.core.routing.operationstrategies.OperationStrategy
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
) : RoutingSource<Routing, State>, Destroyable, BackPressHandler by backPressHandler {

    // can't pass it in constructor, it makes harder to restore it from save instance state
    // TODO: think about how we can avoid keeping unnecessary object after state initialization
    protected abstract val initialState: RoutingElements<Routing, State>

    private val _state by lazy { MutableStateFlow(initialState) }

    override val elements: StateFlow<RoutingElements<Routing, State>>
        get() = _state

    private val onScreenMapper = OnScreenMapper<Routing, State>(scope, screenResolver)

    override val onScreen: StateFlow<RoutingElements<Routing, State>> by lazy(LazyThreadSafetyMode.NONE) {
        onScreenMapper.resolveOnScreenElements(elements)
    }

    override val offScreen: StateFlow<RoutingElements<Routing, State>> by lazy(LazyThreadSafetyMode.NONE) {
        onScreenMapper.resolveOffScreenElements(elements)
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
        _state.update { currentState ->
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

    override fun destroy() {
        scope.cancel()
    }
}
