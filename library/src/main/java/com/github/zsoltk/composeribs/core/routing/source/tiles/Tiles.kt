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
import kotlinx.parcelize.Parcelize

class Tiles<T : Any>(
    initialElements: List<T>,
) : RoutingSource<T, Tiles.TransitionState> {

    enum class TransitionState {
        CREATED, STANDARD, SELECTED, DESTROYED
    }

    @Parcelize
    internal object TilesOnScreenResolver : OnScreenResolver<TransitionState> {
        override fun isOnScreen(state: TransitionState): Boolean =
            when (state) {
                TransitionState.CREATED,
                TransitionState.STANDARD,
                TransitionState.SELECTED -> true
                TransitionState.DESTROYED -> false
            }
    }

    private val state = MutableStateFlow(
        initialElements.map {
            TilesElement(
                onScreenResolver = TilesOnScreenResolver,
                key = RoutingKey(it),
                fromState = TransitionState.CREATED,
                targetState = TransitionState.STANDARD,
                onScreen = true,
                operation = Operation.Noop()
            )
        }
    )

    override val all: StateFlow<TilesElements<T>> =
        state.asStateFlow()

    override val offScreen: StateFlow<TilesElements<T>> =
        MutableStateFlow(emptyList())

    override val onScreen: StateFlow<TilesElements<T>>
        get() = all

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
