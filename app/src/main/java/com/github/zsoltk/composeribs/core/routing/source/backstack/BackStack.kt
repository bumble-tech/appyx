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
import kotlinx.parcelize.RawValue
import java.util.concurrent.atomic.AtomicInteger

class BackStack<T>(
    initialElement: T,
    savedStateMap: SavedStateMap?,
) : RoutingSource<T, BackStack.TransitionState> {

    @Parcelize
    data class LocalRoutingKey<T>(
        override val routing: @RawValue T,
        val uuid: Int,
    ) : RoutingKey<T>, Parcelable

    enum class TransitionState {
        CREATED, ON_SCREEN, STASHED_IN_BACK_STACK, DESTROYED,
    }

    interface Operation<T> : (List<BackStackElement<T>>) -> List<BackStackElement<T>> {

        fun isApplicable(elements: List<BackStackElement<T>>): Boolean
    }

    // TODO Replace with UUID for restoration simplicity?
    private val tmpCounter = AtomicInteger(
        savedStateMap
            ?.restoreHistory()
            ?.maxOf { (it.key as LocalRoutingKey<*>).uuid }
            ?.inc()
            ?: 1
    )

    private val state = MutableStateFlow(
        savedStateMap?.restoreHistory() ?: listOf(
            BackStackElement(
                key = LocalRoutingKey(initialElement, tmpCounter.incrementAndGet()),
                fromState = TransitionState.ON_SCREEN,
                targetState = TransitionState.ON_SCREEN,
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
        state.unsuspendedMap { list -> list.count { it.targetState == TransitionState.STASHED_IN_BACK_STACK } > 0 }

    fun push(element: T) {
        state.update { list ->
            list.map {
                if (it.targetState == TransitionState.ON_SCREEN) {
                    it.copy(targetState = TransitionState.STASHED_IN_BACK_STACK)
                } else {
                    it
                }
            } + BackStackElement(
                key = LocalRoutingKey(element, tmpCounter.incrementAndGet()),
                fromState = TransitionState.CREATED,
                targetState = TransitionState.ON_SCREEN,
                onScreen = true
            )
        }
    }

    fun pop() {
        state.update { list ->
            val destroyIndex = list.indexOfLast { it.targetState == TransitionState.ON_SCREEN }
            val unStashIndex =
                list.indexOfLast { it.targetState == TransitionState.STASHED_IN_BACK_STACK }
            require(destroyIndex != -1) { "Nothing to destroy, state=$list" }
            require(unStashIndex != -1) { "Nothing to remove from stash, state=$list" }
            list.mapIndexed { index, element ->
                when (index) {
                    destroyIndex -> element.copy(targetState = TransitionState.DESTROYED)
                    unStashIndex -> element.copy(
                        targetState = TransitionState.ON_SCREEN,
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
                        TransitionState.DESTROYED ->
                            null
                        TransitionState.STASHED_IN_BACK_STACK ->
                            it.copy(fromState = it.targetState, onScreen = false)
                        TransitionState.CREATED,
                        TransitionState.ON_SCREEN ->
                            it.copy(fromState = it.targetState, onScreen = true)
                    }
                } else {
                    it
                }
            }
        }
    }

    fun perform(operation: Operation<T>) {
        if (operation.isApplicable(state.value)) {
            state.update { operation(it) }
        }
    }

    override fun onBackPressed() {
        pop()
    }

    override fun saveInstanceState(): Any =
        state.value.mapNotNull {
            // Sanitize outputs, removing all transitions
            if (it.targetState != TransitionState.DESTROYED) {
                it.copy(fromState = it.targetState)
            } else {
                null
            }
        }

    private fun SavedStateMap.restoreHistory() =
        this[Node.KEY_ROUTING_SOURCE] as? List<BackStackElement<T>>

}
