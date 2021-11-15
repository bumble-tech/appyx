package com.github.zsoltk.composeribs.core.routing.source.backstack

import com.github.zsoltk.composeribs.core.ParentNode
import com.github.zsoltk.composeribs.core.SavedStateMap
import com.github.zsoltk.composeribs.core.routing.OnScreenResolver
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.pop
import com.github.zsoltk.composeribs.core.unsuspendedMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.parcelize.Parcelize

class BackStack<T : Any>(
    initialElement: T,
    savedStateMap: SavedStateMap?,
    private val key: String = ParentNode.KEY_ROUTING_SOURCE,
) : RoutingSource<T, BackStack.TransitionState> {

    enum class TransitionState {
        CREATED, ON_SCREEN, STASHED_IN_BACK_STACK, DESTROYED,
    }

    interface Operation<T> : (BackStackElements<T>) -> BackStackElements<T> {

        fun isApplicable(elements: BackStackElements<T>): Boolean
    }

    private val state = MutableStateFlow(
        savedStateMap?.restoreHistory() ?: listOf(
            BackStackElement(
                onScreenResolver = BackStackOnScreenResolver,
                key = RoutingKey(initialElement),
                fromState = TransitionState.ON_SCREEN,
                targetState = TransitionState.ON_SCREEN,
                onScreen = true
            )
        )
    )

    override val all: StateFlow<BackStackElements<T>> =
        state

    override val offScreen: StateFlow<BackStackElements<T>> =
        state.unsuspendedMap { list -> list.filter { !it.onScreen } }

    override val onScreen: StateFlow<BackStackElements<T>> =
        state.unsuspendedMap { list -> list.filter { it.onScreen } }

    override val canHandleBackPress: StateFlow<Boolean> =
        state.unsuspendedMap { list -> list.count { it.targetState == TransitionState.STASHED_IN_BACK_STACK } > 0 }

    override fun onTransitionFinished(key: RoutingKey<T>) {
        state.update { list ->
            list.mapNotNull {
                if (it.key == key) {
                    when (it.targetState) {
                        TransitionState.DESTROYED ->
                            null
                        TransitionState.STASHED_IN_BACK_STACK,
                        TransitionState.CREATED,
                        TransitionState.ON_SCREEN ->
                            it.transitionFinished(targetState = it.targetState)
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

    override fun saveInstanceState(savedStateMap: MutableMap<String, Any>) {
        savedStateMap[key] =
            state.value.mapNotNull {
                // Sanitize outputs, removing all transitions
                if (it.targetState != TransitionState.DESTROYED) {
                    it.transitionTo(targetState = it.targetState)
                } else {
                    null
                }
            }
    }

    private fun SavedStateMap.restoreHistory() =
        this[key] as? BackStackElements<T>


    override fun isOnScreen(key: RoutingKey<T>): Boolean =
        state.value.find { it.key == key }?.onScreen ?: false
}
