package com.github.zsoltk.composeribs.core.routing.source.tiles

import android.os.Parcelable
import com.github.zsoltk.composeribs.core.routing.Operation
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.routing.RoutingState
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
        value = RoutingState(
            elements = initialElements.map {
                TilesElement(
                    key = LocalRoutingKey(it, tmpCounter.incrementAndGet()),
                    fromState = TransitionState.CREATED,
                    targetState = TransitionState.STANDARD,
                )
            },
            operation = Operation.Noop()
        )
    )

    override val all: StateFlow<RoutingState<T, TransitionState>> =
        state.asStateFlow()

    override val offScreen: StateFlow<RoutingState<T, TransitionState>> =
        MutableStateFlow(
            value = RoutingState(
                elements = emptyList(),
                operation = Operation.Noop()
            )
        )

    override val onScreen: StateFlow<RoutingState<T, TransitionState>>
        get() = all

    override val canHandleBackPress: StateFlow<Boolean> =
        state.unsuspendedMap { state -> state.elements.any { it.targetState == TransitionState.SELECTED } }

    override fun onTransitionFinished(key: RoutingKey<T>) {
        state.update { state ->
            state.copy(
                elements = state.elements.mapNotNull {
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
            )
        }
    }

    fun perform(operation: TilesOperation<T>) {
        val elements = state.value.elements
        if (operation.isApplicable(elements)) {
            state.update {
                RoutingState(
                    elements = operation(elements, tmpCounter),
                    operation = operation
                )
            }
        }
    }

    override fun onBackPressed() {
        deselectAll()
    }

}
