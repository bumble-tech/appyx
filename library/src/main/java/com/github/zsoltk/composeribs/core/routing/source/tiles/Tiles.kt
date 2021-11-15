package com.github.zsoltk.composeribs.core.routing.source.tiles

import com.github.zsoltk.composeribs.core.routing.OnScreenResolver
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.unsuspendedMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.parcelize.Parcelize

class Tiles<T>(
    initialElements: List<T>,
) : RoutingSource<T, Tiles.TransitionState> {

    enum class TransitionState {
        CREATED, STANDARD, SELECTED, DESTROYED
    }

    @Parcelize
    private object TilesOnScreenResolver : OnScreenResolver<TransitionState> {
        override fun resolve(state: TransitionState): Boolean =
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
                onScreen = true
            )
        }
    )

    override val all: StateFlow<List<TilesElement<T>>> =
        state.asStateFlow()

    override val offScreen: StateFlow<List<TilesElement<T>>> =
        MutableStateFlow(emptyList())

    override val onScreen: StateFlow<List<TilesElement<T>>>
        get() = all

    override val canHandleBackPress: StateFlow<Boolean> =
        state.unsuspendedMap { list -> list.any { it.targetState == TransitionState.SELECTED } }

    fun add(element: T) {
        state.update { list ->
            list + TilesElement(
                onScreenResolver = TilesOnScreenResolver,
                key = RoutingKey(element),
                fromState = TransitionState.CREATED,
                targetState = TransitionState.STANDARD,
                onScreen = true
            )
        }
    }

    fun select(key: RoutingKey<T>) {
        state.update { list ->
            list.map {
                if (it.key == key && it.targetState == TransitionState.STANDARD) {
                    it.transitionTo(targetState = TransitionState.SELECTED)
                } else {
                    it
                }
            }
        }
    }

    fun deselect(key: RoutingKey<T>) {
        state.update { list ->
            list.map {
                if (it.key == key && it.targetState == TransitionState.SELECTED) {
                    it.transitionTo(targetState = TransitionState.STANDARD)
                } else {
                    it
                }
            }
        }
    }

    fun deselectAll() {
        state.update { list ->
            list.map {
                if (it.targetState == TransitionState.SELECTED) {
                    it.transitionTo(targetState = TransitionState.STANDARD)
                } else {
                    it
                }
            }
        }
    }

    fun toggleSelection(key: RoutingKey<T>) {
        state.update { list ->
            list.map {
                if (it.key == key) {
                    when (it.targetState) {
                        TransitionState.SELECTED -> it.transitionTo(targetState = TransitionState.STANDARD)
                        TransitionState.STANDARD -> it.transitionTo(targetState = TransitionState.SELECTED)
                        else -> it
                    }
                } else {
                    it
                }
            }
        }
    }

    fun removeSelected() {
        state.update { list ->
            list.map {
                if (it.targetState == TransitionState.SELECTED) {
                    it.transitionTo(targetState = TransitionState.DESTROYED)
                } else {
                    it
                }
            }
        }
    }

    fun destroy(key: RoutingKey<T>) {
        state.update { list ->
            list.map {
                if (it.key == key) {
                    it.transitionTo(targetState = TransitionState.DESTROYED)
                } else {
                    it
                }
            }
        }
    }

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

    override fun onBackPressed() {
        deselectAll()
    }

}
