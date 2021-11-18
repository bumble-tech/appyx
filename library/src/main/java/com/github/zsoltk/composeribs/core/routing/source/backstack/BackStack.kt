package com.github.zsoltk.composeribs.core.routing.source.backstack

import com.github.zsoltk.composeribs.core.node.ParentNode
import com.github.zsoltk.composeribs.core.routing.OnScreenResolver
import com.github.zsoltk.composeribs.core.routing.Operation
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.routing.RoutingSourceAdapterImpl
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.pop
import com.github.zsoltk.composeribs.core.state.SavedStateMap
import com.github.zsoltk.composeribs.core.unsuspendedMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class BackStack<T : Any>(
    initialElement: T,
    savedStateMap: SavedStateMap?,
    private val key: String = ParentNode.KEY_ROUTING_SOURCE,
) : RoutingSource<T, TransitionState> {

    enum class TransitionState {
        CREATED, ON_SCREEN, STASHED_IN_BACK_STACK, DESTROYED,
    }

    private val state = MutableStateFlow(
        value = savedStateMap?.restoreHistory() ?: listOf(
            BackStackElement(
                key = RoutingKey(initialElement),
                fromState = TransitionState.ON_SCREEN,
                targetState = TransitionState.ON_SCREEN,
                operation = Operation.Noop()
            )
        )
    )

    override val elements: StateFlow<BackStackElements<T>> =
        state

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
                            it.onTransitionFinished(targetState = it.targetState)
                    }
                } else {
                    it
                }
            }
        }
    }

    fun perform(operation: BackStackOperation<T>) {
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
                    it.onTransitionFinished(targetState = it.targetState)
                } else {
                    null
                }
            }
    }

    private fun SavedStateMap.restoreHistory() =
        this[key] as? BackStackElements<T>


}

fun <T : Any> BackStack<T>.adapter(
    routingSourceResolver: OnScreenResolver<TransitionState> = BackStackOnScreenResolver
) =
    RoutingSourceAdapterImpl(
        routingSource = this,
        routingSourceResolver
    )
