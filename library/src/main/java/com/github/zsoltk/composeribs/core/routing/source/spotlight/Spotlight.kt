package com.github.zsoltk.composeribs.core.routing.source.spotlight

import android.os.Parcelable
import android.util.Log
import com.github.zsoltk.composeribs.core.node.ParentNode
import com.github.zsoltk.composeribs.core.plugin.Destroyable
import com.github.zsoltk.composeribs.core.routing.OnScreenResolver
import com.github.zsoltk.composeribs.core.routing.Operation
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.routing.RoutingSourceAdapter
import com.github.zsoltk.composeribs.core.routing.adapter
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

class Spotlight<T : Parcelable, E : Parcelable>(
    items: SpotlightItems<T, E>,
    savedStateMap: SavedStateMap?,
    private val key: String = ParentNode.KEY_ROUTING_SOURCE,
    private val onScreenResolver: OnScreenResolver<TransitionState> = SpotlightOnScreenResolver()
) : RoutingSource<T, TransitionState>, Destroyable {

    enum class TransitionState {
        INACTIVE_BEFORE, ACTIVE, INACTIVE_AFTER
    }

    private val initialKey = items.initialElementKey

    private val state = MutableStateFlow(
        value = savedStateMap?.restoreHistory() ?: items.toSpotlightElements()
    )

    private val scope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined)

    override val adapter: RoutingSourceAdapter<T, TransitionState> by lazy(LazyThreadSafetyMode.NONE) {
        adapter(scope, onScreenResolver)
    }

    override val elements: StateFlow<SpotlightElements<T>> =
        state

    override val canHandleBackPress: StateFlow<Boolean> = //TODO: should handle it?
        state.map { it.firstOrNull()?.key == initialKey }
            .stateIn(scope, SharingStarted.Eagerly, false)

    init {
        scope.launch {
            elements.collect {
                Log.d("SPOTLIGHT", it.joinToString("\n"))
            }
        }
    }

    override fun onBackPressed() {
        //TODO("Not yet implemented")
    }

    override fun onTransitionFinished(key: RoutingKey<T>) {
        state.update { elements ->
            Log.d("SPOTLIGHT", "OnTransitionFinishedUpdate: $key")
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
        Log.d("SPOTLIGHT", "ACCEPT: $operation")
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

        val elements = items.mapIndexed { index, item ->
            val state = when {
                index < activeIndex -> INACTIVE_BEFORE
                index == activeIndex -> ACTIVE
                else -> INACTIVE_AFTER
            }
            SpotlightElement(
                key = RoutingKey(item.element),
                fromState = state,
                targetState = state,
                operation = Operation.Noop()
            )
        }
        Log.d("SPOTLIGHT", elements.joinToString("\n"))

        return elements
    }
}