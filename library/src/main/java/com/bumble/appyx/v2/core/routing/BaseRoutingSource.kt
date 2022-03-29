package com.bumble.appyx.v2.core.routing

import com.bumble.appyx.v2.core.plugin.BackPressHandler
import com.bumble.appyx.v2.core.plugin.Destroyable
import com.bumble.appyx.v2.core.routing.backpresshandlerstrategies.BackPressHandlerStrategy
import com.bumble.appyx.v2.core.routing.backpresshandlerstrategies.DontHandleBackPress
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
    screenResolver: OnScreenStateResolver<State>,
    protected val scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined),
) : RoutingSource<Routing, State>, Destroyable, BackPressHandler by backPressHandler {

    init {
        backPressHandler.init(this, scope)
        operationStrategy.init(this, scope, ::execute)
    }

    protected abstract val state: MutableStateFlow<RoutingElements<Routing, State>>

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

    override fun destroy() {
        scope.cancel()
    }
}
