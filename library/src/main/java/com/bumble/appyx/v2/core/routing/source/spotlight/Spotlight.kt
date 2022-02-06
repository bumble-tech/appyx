package com.bumble.appyx.v2.core.routing.source.spotlight

import android.os.Parcelable
import com.bumble.appyx.v2.core.node.ParentNode
import com.bumble.appyx.v2.core.plugin.Destroyable
import com.bumble.appyx.v2.core.routing.OnScreenMapper
import com.bumble.appyx.v2.core.routing.Operation
import com.bumble.appyx.v2.core.routing.RoutingKey
import com.bumble.appyx.v2.core.routing.RoutingSource
import com.bumble.appyx.v2.core.routing.source.spotlight.Spotlight.TransitionState
import com.bumble.appyx.v2.core.routing.source.spotlight.Spotlight.TransitionState.ACTIVE
import com.bumble.appyx.v2.core.routing.source.spotlight.Spotlight.TransitionState.INACTIVE_AFTER
import com.bumble.appyx.v2.core.routing.source.spotlight.Spotlight.TransitionState.INACTIVE_BEFORE
import com.bumble.appyx.v2.core.routing.source.spotlight.operations.SpotlightOperation
import com.bumble.appyx.v2.core.state.SavedStateMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.coroutines.EmptyCoroutineContext

class Spotlight<T : Parcelable>(
    items: List<T>,
    initialActiveItem: Int = 0,
    savedStateMap: SavedStateMap?,
    private val key: String = ParentNode.KEY_ROUTING_SOURCE,
) : RoutingSource<T, TransitionState>, Destroyable {

    enum class TransitionState {
        INACTIVE_BEFORE, ACTIVE, INACTIVE_AFTER;
    }

    private val state = MutableStateFlow(
        value = savedStateMap?.restoreHistory() ?: items.toSpotlightElements(initialActiveItem)
    )

    private val scope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined)

    private val onScreenMapper =
        OnScreenMapper<T, TransitionState>(scope, SpotlightOnScreenResolver)

    override val onScreen: StateFlow<SpotlightElements<T>> =
        onScreenMapper.resolveOnScreenElements(state)

    override val offScreen: StateFlow<SpotlightElements<T>> =
        onScreenMapper.resolveOffScreenElements(state)

    override val elements: StateFlow<SpotlightElements<T>> =
        state

    override val canHandleBackPress: StateFlow<Boolean> = MutableStateFlow(false)

    override fun onBackPressed() {
        //No-op
    }

    override fun onTransitionFinished(key: RoutingKey<T>) {
        state.update { elements ->
            elements.map {
                if (key == it.key) {
                    it.onTransitionFinished()
                } else {
                    it
                }
            }
        }
    }

    override fun accept(operation: SpotlightOperation<T>) {
        if (operation.isApplicable(state.value)) {
            state.update { operation(it) }
        }
    }

    override fun destroy() {
        scope.cancel()
    }

    override fun saveInstanceState(savedStateMap: MutableMap<String, Any>) {
        savedStateMap[key] =
            state.value.map {
                if (it.targetState != it.fromState) {
                    it.onTransitionFinished()
                }
                it
            }
    }

    private fun SavedStateMap.restoreHistory() =
        this[key] as? SpotlightElements<T>

    private fun List<T>.toSpotlightElements(activeIndex: Int): SpotlightElements<T> =
        mapIndexed { index, item ->
            val state = when {
                index < activeIndex -> INACTIVE_BEFORE
                index == activeIndex -> ACTIVE
                else -> INACTIVE_AFTER
            }
            SpotlightElement(
                key = RoutingKey(item),
                fromState = state,
                targetState = state,
                operation = Operation.Noop()
            )
        }
}
