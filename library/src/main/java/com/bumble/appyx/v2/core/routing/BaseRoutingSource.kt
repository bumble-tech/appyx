package com.bumble.appyx.v2.core.routing

import android.util.Log
import com.bumble.appyx.v2.core.plugin.Destroyable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlin.coroutines.EmptyCoroutineContext


abstract class BaseRoutingSource<Routing, State>(
    val plugins: List<RoutingPlugin<Routing, State>>,
    screenResolver: OnScreenStateResolver<State>,
    protected val scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined),
) : RoutingSource<Routing, State>, Destroyable {

    init {
        plugins.forEach { it.init(this, scope) }
    }

    abstract val state: MutableStateFlow<RoutingElements<Routing, State>>

    private val onScreenMapper = OnScreenMapper<Routing, State>(scope, screenResolver)

    override val onScreen: StateFlow<RoutingElements<Routing, State>> by lazy {
        onScreenMapper.resolveOnScreenElements(state)
    }

    override val offScreen: StateFlow<RoutingElements<Routing, State>> by lazy {
        onScreenMapper.resolveOffScreenElements(state)
    }

    private val backPressHandler: BackPressHandler<Routing, State>? =
        plugins<Routing, State, BackPressHandler<Routing, State>>().let {
            if (it.size > 1) {
                Log.w(
                    this::class.simpleName,
                    "Multiple backPressHandler plugins were passed. Only first one will be used "
                )
            }
            it.firstOrNull()
        }

    private val operationStrategy: OperationStrategy<Routing, State>? =
        plugins<Routing, State, OperationStrategy<Routing, State>>().let {
            if (it.size > 1) {
                Log.w(
                    this::class.simpleName,
                    "Multiple operationStrategy plugins were passed. Only first one will be used "
                )
            }
            it.firstOrNull()
        }

    override val canHandleBackPress: StateFlow<Boolean> by lazy {
        backPressHandler?.canHandleBackPress
            ?: flowOf(false).stateIn(scope, SharingStarted.Eagerly, false)
    }

    override fun onBackPressed() {
        backPressHandler?.onBackPressed()
    }

    override fun accept(operation: Operation<Routing, State>) {
        operationStrategy?.accept(operation) ?: execute(operation)
    }

    fun execute(operation: Operation<Routing, State>) {
        if (operation.isApplicable(state.value)) {
            state.update { operation(it) }
        }
    }

    override fun destroy() {
        scope.cancel()
    }
}
