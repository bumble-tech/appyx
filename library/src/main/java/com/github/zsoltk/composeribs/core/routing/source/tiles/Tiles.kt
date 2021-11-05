package com.github.zsoltk.composeribs.core.routing.source.tiles

import android.os.Parcelable
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.routing.UuidGenerator
import com.github.zsoltk.composeribs.core.routing.source.tiles.operation.deselectAll
import com.github.zsoltk.composeribs.core.unsuspendedMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

class Tiles<T : Any>(
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

    private val tmpCounter = UuidGenerator(1)

    private val state = MutableStateFlow(
        initialElements.map {
            TilesElement(
                key = LocalRoutingKey(it, tmpCounter.incrementAndGet()),
                fromState = TransitionState.CREATED,
                targetState = TransitionState.STANDARD,
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

    fun perform(operation: TilesOperation<T>) {
        if (operation.isApplicable(state.value)) {
            state.update { operation(it, tmpCounter) }
        }
    }

    override fun onBackPressed() {
        deselectAll()
    }

}
