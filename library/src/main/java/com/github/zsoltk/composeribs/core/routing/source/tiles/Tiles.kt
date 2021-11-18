package com.github.zsoltk.composeribs.core.routing.source.tiles

import com.github.zsoltk.composeribs.core.routing.Operation
import com.github.zsoltk.composeribs.core.routing.OnScreenResolver
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.routing.source.tiles.operation.deselectAll
import com.github.zsoltk.composeribs.core.unsuspendedMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class Tiles<T : Any>(
    initialElements: List<T>,
) : RoutingSource<T, Tiles.TransitionState> {

    enum class TransitionState {
        CREATED, STANDARD, SELECTED, DESTROYED
    }

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

    override val elements: StateFlow<TilesElements<T>> =
        state.asStateFlow()

    override val canHandleBackPress: StateFlow<Boolean> =
        state.unsuspendedMap { list -> list.any { it.targetState == TransitionState.SELECTED } }

    override fun onTransitionFinished(key: RoutingKey<T>) {
        state.update { list ->
            list.mapNotNull {
                if (it.key == key) {
                    if (it.targetState == TransitionState.DESTROYED) {
                        null
                    } else {
                        it.transitionFinished(targetState = it.targetState)
                    }
                } else {
                    it
                }
            }
        }
    }

    fun perform(operation: TilesOperation<T>) {
        if (operation.isApplicable(state.value)) {
            state.update { operation(it) }
        }
    }

    override fun onBackPressed() {
        deselectAll()
    }
}
