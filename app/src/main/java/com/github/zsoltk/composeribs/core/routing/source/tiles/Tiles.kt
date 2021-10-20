package com.github.zsoltk.composeribs.core.routing.source.tiles

import android.os.Parcelable
import com.github.zsoltk.composeribs.core.routing.RoutingElement
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.unsuspendedMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.util.concurrent.atomic.AtomicInteger

class Tiles<T>(
    initialElements: List<T>,
) : RoutingSource<T, Tiles.TransitionState> {

    @Parcelize
    data class LocalRoutingKey<T>(
        override val routing: @RawValue T,
        val uuid: Int,
    ) : RoutingKey<T>, Parcelable

    enum class TransitionState {
        CREATED, STANDARD, SELECTED, DESTROYED
    }

    private val tmpCounter = AtomicInteger(1)

    private val state = MutableStateFlow(
        initialElements.map {
            TilesElement(
                key = LocalRoutingKey(it, tmpCounter.incrementAndGet()),
                fromState = TransitionState.CREATED,
                targetState = TransitionState.STANDARD,
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
        state.unsuspendedMap { list -> list.any { it.targetState == TransitionState.SELECTED } }

    fun add(element: T) {
        state.update { list ->
            list + TilesElement(
                key = LocalRoutingKey(element, tmpCounter.incrementAndGet()),
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
                    it.copy(targetState = TransitionState.SELECTED)
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
                    it.copy(targetState = TransitionState.STANDARD)
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
                    it.copy(targetState = TransitionState.STANDARD)
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
                        TransitionState.SELECTED -> it.copy(targetState = TransitionState.STANDARD)
                        TransitionState.STANDARD -> it.copy(targetState = TransitionState.SELECTED)
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
                    it.copy(targetState = TransitionState.DESTROYED)
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
                    it.copy(targetState = TransitionState.DESTROYED)
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
