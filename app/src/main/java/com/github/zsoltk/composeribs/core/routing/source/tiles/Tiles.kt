package com.github.zsoltk.composeribs.core.routing.source.tiles

import com.github.zsoltk.composeribs.core.routing.RoutingElement
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.routing.source.tiles.Tiles.TransitionState.CREATED
import com.github.zsoltk.composeribs.core.routing.source.tiles.Tiles.TransitionState.DESTROYED
import com.github.zsoltk.composeribs.core.routing.source.tiles.Tiles.TransitionState.SELECTED
import com.github.zsoltk.composeribs.core.routing.source.tiles.Tiles.TransitionState.STANDARD
import com.github.zsoltk.composeribs.core.unsuspendedMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicInteger

open class Tiles<T>(
    initialElements: List<T>,
) : RoutingSource<T, Tiles.TransitionState> {

    data class LocalRoutingKey<T>(
        override val routing: T,
        val uuid: Int,
    ) : RoutingKey<T>

    enum class TransitionState {
        CREATED, STANDARD, SELECTED, DESTROYED
    }

    private val tmpCounter = AtomicInteger(1)

    private val state = MutableStateFlow(
        initialElements.map {
            TilesElement(
                key = LocalRoutingKey(it, tmpCounter.incrementAndGet()),
                fromState = CREATED,
                targetState = STANDARD,
                onScreen = true
            )
        }
    )

    override val all: StateFlow<List<RoutingElement<T, TransitionState>>> =
        state.asStateFlow()

    override val offScreen: StateFlow<List<TilesElement<T>>> =
        MutableStateFlow(emptyList())

    override val onScreen: StateFlow<List<TilesElement<T>>>
        get() = all

    override val canHandleBackPress: StateFlow<Boolean> =
        state.unsuspendedMap { list -> list.any { it.targetState == SELECTED } }

    fun add(element: T) {
        state.update { list ->
            list + TilesElement(
                key = LocalRoutingKey(element, tmpCounter.incrementAndGet()),
                fromState = CREATED,
                targetState = STANDARD,
                onScreen = true
            )
        }
    }

    fun select(key: RoutingKey<T>) {
        state.update { list ->
            list.map {
                if (it.key == key && it.targetState == STANDARD) {
                    it.copy(targetState = SELECTED)
                } else {
                    it
                }
            }
        }
    }

    fun deselect(key: RoutingKey<T>) {
        state.update { list ->
            list.map {
                if (it.key == key && it.targetState == SELECTED) {
                    it.copy(targetState = STANDARD)
                } else {
                    it
                }
            }
        }
    }

    fun deselectAll() {
        state.update { list ->
            list.map {
                if (it.targetState == SELECTED) {
                    it.copy(targetState = STANDARD)
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
                        SELECTED -> it.copy(targetState = STANDARD)
                        STANDARD -> it.copy(targetState = SELECTED)
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
                if (it.targetState == SELECTED) {
                    it.copy(targetState = DESTROYED)
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
                    it.copy(targetState = DESTROYED)
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
                    if (it.targetState == DESTROYED) {
                        null
                    } else {
                        it.copy(fromState = it.targetState)
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
