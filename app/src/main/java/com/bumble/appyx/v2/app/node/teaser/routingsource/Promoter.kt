package com.bumble.appyx.v2.app.node.teaser.routingsource

import com.bumble.appyx.v2.core.plugin.Destroyable
import com.bumble.appyx.v2.core.routing.OnScreenMapper
import com.bumble.appyx.v2.core.routing.Operation
import com.bumble.appyx.v2.core.routing.RoutingKey
import com.bumble.appyx.v2.core.routing.RoutingSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlin.coroutines.EmptyCoroutineContext

class Promoter<T : Any>(
    initialElements: List<T> = listOf(),
) : RoutingSource<T, Promoter.TransitionState>, Destroyable {

    enum class TransitionState {
        CREATED, STAGE1, STAGE2, STAGE3, STAGE4, SELECTED, DESTROYED;

        fun next(): TransitionState =
            when (this) {
                CREATED -> STAGE1
                STAGE1 -> STAGE2
                STAGE2 -> STAGE3
                STAGE3 -> STAGE4
                STAGE4 -> SELECTED
                SELECTED -> DESTROYED
                DESTROYED -> DESTROYED
            }
    }

    private val scope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined)

    private val state = MutableStateFlow(
        initialElements.map {
            PromoterElement(
                key = RoutingKey(it),
                fromState = TransitionState.CREATED,
                targetState = TransitionState.STAGE1,
                operation = Operation.Noop()
            )
        }
    )

    private val onScreenMapper = OnScreenMapper<T, TransitionState>(scope, PromoterOnScreenResolver)

    override val elements: StateFlow<PromoterElements<T>>
        get() = state

    override val onScreen: StateFlow<PromoterElements<T>> =
        onScreenMapper.resolveOnScreenElements(state)

    override val offScreen: StateFlow<PromoterElements<T>> =
        onScreenMapper.resolveOffScreenElements(state)

    override val canHandleBackPress: StateFlow<Boolean> =
        state.map { list -> list.any { it.targetState == TransitionState.STAGE2 } }
            .stateIn(scope, SharingStarted.Eagerly, false)

    override fun onTransitionFinished(key: RoutingKey<T>) {
        state.update { list ->
            list.mapNotNull {
                if (it.key == key) {
                    if (it.targetState == TransitionState.DESTROYED) {
                        null
                    } else {
                        it.onTransitionFinished()
                    }
                } else {
                    it
                }
            }
        }
    }

    override fun accept(operation: Operation<T, TransitionState>) {
        if (operation.isApplicable(state.value)) {
            state.update { operation(it) }
        }
    }

    override fun onBackPressed() {
        // no-op
    }

    override fun destroy() {
        scope.cancel()
    }

}
