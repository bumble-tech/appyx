package com.github.zsoltk.composeribs.core.routing.source.backstack

import android.os.Parcelable
import com.github.zsoltk.composeribs.core.Node
import com.github.zsoltk.composeribs.core.SavedStateMap
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.unsuspendedMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.parcelize.Parcelize
import java.util.concurrent.atomic.AtomicInteger

class BackStack<T : Parcelable>(
    initialElement: T,
    savedStateMap: SavedStateMap?,
) : RoutingSource<T, BackStack.TransitionState> {

    @Parcelize
    data class LocalRoutingKey<T : Parcelable>(
        override val routing: T,
        val uuid: Int,
    ) : RoutingKey<T>

    sealed class TransitionState : Parcelable {
        @Parcelize
        object Created : TransitionState()

        @Parcelize
        object OnScreen : TransitionState()

        @Parcelize
        object StashedInBackstack : TransitionState()

        @Parcelize
        object Destroyed : TransitionState()
    }

    private val tmpCounter = AtomicInteger(1)

    private val state = MutableStateFlow(
        savedStateMap?.restoreHistory() ?: listOf(
            BackStackElement(
                key = LocalRoutingKey(initialElement, tmpCounter.incrementAndGet()),
                fromState = TransitionState.OnScreen,
                targetState = TransitionState.OnScreen,
                onScreen = true,
            )
        )
    )

    override val all: StateFlow<List<BackStackElement<T>>> =
        state

    override val offScreen: StateFlow<List<BackStackElement<T>>> =
        state.unsuspendedMap { list -> list.filter { !it.onScreen } }

    override val onScreen: StateFlow<List<BackStackElement<T>>> =
        state.unsuspendedMap { list -> list.filter { it.onScreen } }

    override val canHandleBackPress: StateFlow<Boolean> =
        state.unsuspendedMap { list -> list.count { it.targetState == TransitionState.StashedInBackstack } > 0 }

    fun push(element: T) {
        state.update { list ->
            list.map {
                if (it.targetState == TransitionState.OnScreen) {
                    it.copy(targetState = TransitionState.StashedInBackstack)
                } else {
                    it
                }
            } + BackStackElement(
                key = LocalRoutingKey(element, tmpCounter.incrementAndGet()),
                fromState = TransitionState.Created,
                targetState = TransitionState.OnScreen,
                onScreen = true
            )
        }
    }

    fun pop() {
        state.update { list ->
            val destroyIndex = list.indexOfLast { it.targetState == TransitionState.OnScreen }
            val unStashIndex =
                list.indexOfLast { it.targetState == TransitionState.StashedInBackstack }
            require(destroyIndex != -1) { "Nothing to destroy, state=$list" }
            require(unStashIndex != -1) { "Nothing to remove from stash, state=$list" }
            list.mapIndexed { index, element ->
                when (index) {
                    destroyIndex -> element.copy(targetState = TransitionState.Destroyed)
                    unStashIndex -> element.copy(
                        targetState = TransitionState.OnScreen,
                        onScreen = true
                    )
                    else -> element
                }
            }
        }
    }

    override fun onTransitionFinished(key: RoutingKey<T>) {
        state.update { list ->
            list.mapNotNull {
                if (it.key == key) {
                    when (it.targetState) {
                        TransitionState.Destroyed ->
                            null
                        TransitionState.StashedInBackstack ->
                            it.copy(fromState = it.targetState, onScreen = false)
                        TransitionState.Created,
                        TransitionState.OnScreen ->
                            it.copy(fromState = it.targetState, onScreen = true)
                    }
                } else {
                    it
                }
            }
        }
    }

    override fun onBackPressed() {
        pop()
    }

    override fun saveInstanceState(): Any =
        state.value.mapNotNull {
            // Sanitize outputs, removing all transitions
            if (it.targetState != TransitionState.Destroyed) {
                it.copy(fromState = it.targetState)
            } else {
                null
            }
        }

    private fun SavedStateMap.restoreHistory() =
        this[Node.KEY_ROUTING_SOURCE] as? List<BackStackElement<T>>

}
