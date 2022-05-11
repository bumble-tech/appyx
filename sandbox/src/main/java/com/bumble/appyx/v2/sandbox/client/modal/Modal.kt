package com.bumble.appyx.v2.sandbox.client.modal

import com.bumble.appyx.v2.sandbox.client.modal.Modal.TransitionState
import com.bumble.appyx.v2.sandbox.client.modal.Modal.TransitionState.CREATED
import com.bumble.appyx.v2.sandbox.client.modal.Modal.TransitionState.DESTROYED
import com.bumble.appyx.v2.sandbox.client.modal.Modal.TransitionState.FULL_SCREEN
import com.bumble.appyx.v2.sandbox.client.modal.Modal.TransitionState.MODAL
import com.bumble.appyx.v2.sandbox.client.modal.operation.revert
import com.bumble.appyx.v2.core.routing.onscreen.OnScreenMapper
import com.bumble.appyx.v2.core.routing.Operation
import com.bumble.appyx.v2.core.routing.Operation.Noop
import com.bumble.appyx.v2.core.routing.RoutingElements
import com.bumble.appyx.v2.core.routing.RoutingKey
import com.bumble.appyx.v2.core.routing.RoutingSource
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class Modal<T : Any>(initialElement: T) : RoutingSource<T, TransitionState> {

    enum class TransitionState {
        CREATED, MODAL, FULL_SCREEN, DESTROYED
    }

    private val scope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined)
    private val onScreenMapper = OnScreenMapper<T, TransitionState>(scope, ModalOnScreenResolver)

    private val state = MutableStateFlow(
        value = listOf(
            ModalElement(
                key = RoutingKey(initialElement),
                fromState = CREATED,
                targetState = CREATED,
                operation = Noop()
            )
        )

    )

    override val elements: StateFlow<RoutingElements<T, out TransitionState>>
        get() = state

    override val onScreen: StateFlow<ModalElements<T>> =
        onScreenMapper.resolveOnScreenElements(state)

    override val offScreen: StateFlow<ModalElements<T>> =
        onScreenMapper.resolveOffScreenElements(state)

    override fun accept(operation: Operation<T, TransitionState>) {
        if (operation.isApplicable(state.value)) {
            state.update { operation(it) }
        }
    }

    override fun onTransitionFinished(key: RoutingKey<T>) {
        state.update { list ->
            list.mapNotNull {
                if (it.key == key) {
                    when (it.targetState) {
                        MODAL,
                        CREATED,
                        FULL_SCREEN -> it.onTransitionFinished()
                        DESTROYED -> null
                    }
                } else {
                    it
                }
            }
        }
    }

    override val canHandleBackPress: StateFlow<Boolean> =
        state
            .map { elements ->
                elements.any { it.targetState == FULL_SCREEN }
            }
            .stateIn(scope, SharingStarted.Eagerly, false)

    override fun onBackPressed() {
        revert()
    }
}
