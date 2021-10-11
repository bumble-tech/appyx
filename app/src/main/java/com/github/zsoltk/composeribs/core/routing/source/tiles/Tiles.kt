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
import java.util.concurrent.atomic.AtomicInteger

class Tiles<T : Parcelable>(
    initialElements: List<T>,
) : RoutingSource<T, Tiles.TransitionState> {

    @Parcelize
    data class LocalRoutingKey<T : Parcelable>(
        override val routing: T,
        val uuid: Int,
    ) : RoutingKey<T>

    sealed class TransitionState : Parcelable {
        @Parcelize
        object Created : TransitionState()

        @Parcelize
        object Standard : TransitionState()

        @Parcelize
        object Selected : TransitionState()

        @Parcelize
        object Destroyed : TransitionState()
    }

    private val tmpCounter = AtomicInteger(1)

    private val state = MutableStateFlow(
        initialElements.map {
            TilesElement(
                key = LocalRoutingKey(it, tmpCounter.incrementAndGet()),
                fromState = TransitionState.Created,
                targetState = TransitionState.Standard,
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
        state.unsuspendedMap { list -> list.any { it.targetState == TransitionState.Selected } }

    fun add(element: T) {
        state.update { list ->
            list + TilesElement(
                key = LocalRoutingKey(element, tmpCounter.incrementAndGet()),
                fromState = TransitionState.Created,
                targetState = TransitionState.Standard,
                onScreen = true
            )
        }
    }

    fun select(key: RoutingKey<T>) {
        state.update { list ->
            list.map {
                if (it.key == key && it.targetState == TransitionState.Standard) {
                    it.copy(targetState = TransitionState.Selected)
                } else {
                    it
                }
            }
        }
    }

    fun deselect(key: RoutingKey<T>) {
        state.update { list ->
            list.map {
                if (it.key == key && it.targetState == TransitionState.Selected) {
                    it.copy(targetState = TransitionState.Standard)
                } else {
                    it
                }
            }
        }
    }

    fun deselectAll() {
        state.update { list ->
            list.map {
                if (it.targetState == TransitionState.Selected) {
                    it.copy(targetState = TransitionState.Standard)
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
                        TransitionState.Selected -> it.copy(targetState = TransitionState.Standard)
                        TransitionState.Standard -> it.copy(targetState = TransitionState.Selected)
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
                if (it.targetState == TransitionState.Selected) {
                    it.copy(targetState = TransitionState.Destroyed)
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
                    it.copy(targetState = TransitionState.Destroyed)
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
                    if (it.targetState == TransitionState.Destroyed) {
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
