package com.github.zsoltk.composeribs.core.routing.source.spotlight

import android.os.Parcelable
import com.github.zsoltk.composeribs.core.node.ParentNode
import com.github.zsoltk.composeribs.core.plugin.Destroyable
import com.github.zsoltk.composeribs.core.routing.OnScreenMapper
import com.github.zsoltk.composeribs.core.routing.Operation
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.routing.source.spotlight.Spotlight.TransitionState
import com.github.zsoltk.composeribs.core.routing.source.spotlight.Spotlight.TransitionState.ACTIVE
import com.github.zsoltk.composeribs.core.routing.source.spotlight.Spotlight.TransitionState.INACTIVE_AFTER
import com.github.zsoltk.composeribs.core.routing.source.spotlight.Spotlight.TransitionState.INACTIVE_BEFORE
import com.github.zsoltk.composeribs.core.routing.source.spotlight.operations.SpotlightOperation
import com.github.zsoltk.composeribs.core.state.SavedStateMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

class Spotlight<T : Parcelable, E : Enum<E>>(
    items: SpotlightItems<T, E>,
    savedStateMap: SavedStateMap?,
    private val key: String = ParentNode.KEY_ROUTING_SOURCE,
) : RoutingSource<T, TransitionState>, Destroyable {

    enum class TransitionState {
        INACTIVE_BEFORE, ACTIVE, INACTIVE_AFTER;
    }

    private val state = MutableStateFlow(
        value = savedStateMap?.restoreHistory() ?: items.toSpotlightElements()
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
        //Not required
        //TODO: should we decouple backpress from RoutingSource base interface?
    }

    init {
        scope.launch {
            offScreen.collect { offElements ->
                val inactiveChanges = offElements.filter { it.fromState != it.targetState }
                state.update { elements ->
                    elements.map {
                        if (it in inactiveChanges) {
                            it.onTransitionFinished()
                        } else {
                            it
                        }
                    }
                }
            }
        }
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

    private fun SavedStateMap.restoreHistory() =
        this[key] as? SpotlightElements<T>

    private fun SpotlightItems<T, E>.toSpotlightElements(): SpotlightElements<T> {

        val activeIndex =
            items.indexOfFirst { it.key == initialElementKey }.let { if (it < 0) 0 else it }

        return items.mapIndexed { index, item ->
            val state = when {
                index < activeIndex -> INACTIVE_BEFORE
                index == activeIndex -> ACTIVE
                else -> INACTIVE_AFTER
            }
            SpotlightElement(
                key = RoutingKey(item.element, item.key.toString()),
                fromState = state,
                targetState = state,
                operation = Operation.Noop()
            )
        }
    }
}