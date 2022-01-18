package com.github.zsoltk.composeribs.core.routing.source.tiles

import com.github.zsoltk.composeribs.core.plugin.Destroyable
import com.github.zsoltk.composeribs.core.routing.OnScreenMapper
import com.github.zsoltk.composeribs.core.routing.Operation
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.routing.source.tiles.operation.deselectAll
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

class Tiles<T : Any>(
    initialElements: List<T>,
) : RoutingSource<T, Tiles.TransitionState>, Destroyable {

    enum class TransitionState {
        CREATED, STANDARD, SELECTED, DESTROYED
    }

    private val scope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined)

    private val state = MutableStateFlow(
        initialElements.map {
            TilesElement(
                key = RoutingKey(it),
                fromState = TransitionState.CREATED,
                targetState = TransitionState.STANDARD,
                operation = Operation.Noop()
            )
        }
    )

    private val onScreenMapper = OnScreenMapper<T, TransitionState>(scope, TilesOnScreenResolver)

    override val elements: StateFlow<TilesElements<T>>
        get() = state

    override val onScreen: StateFlow<TilesElements<T>> =
        onScreenMapper.resolveOnScreenElements(state)

    override val offScreen: StateFlow<TilesElements<T>> =
        onScreenMapper.resolveOffScreenElements(state)

    override val canHandleBackPress: StateFlow<Boolean> =
        state.map { list -> list.any { it.targetState == TransitionState.SELECTED } }
            .stateIn(scope, SharingStarted.Eagerly, false)

    override fun onTransitionFinished(key: RoutingKey<T>) {
        state.update { list ->
            list.mapNotNull {
                if (it.key == key) {
                    if (it.targetState == TransitionState.DESTROYED) {
                        null
                    } else {
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

    override fun onBackPressed() {
        deselectAll()
    }

    override fun destroy() {
        scope.cancel()
    }

}
