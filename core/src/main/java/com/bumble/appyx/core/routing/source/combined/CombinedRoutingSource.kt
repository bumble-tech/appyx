package com.bumble.appyx.core.routing.source.combined

import androidx.activity.OnBackPressedCallback
import com.bumble.appyx.core.plugin.Destroyable
import com.bumble.appyx.core.routing.RoutingElements
import com.bumble.appyx.core.routing.RoutingKey
import com.bumble.appyx.core.routing.RoutingSource
import com.bumble.appyx.core.routing.RoutingSourceAdapter
import com.bumble.appyx.core.state.MutableSavedStateMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlin.coroutines.EmptyCoroutineContext

class CombinedRoutingSource<Routing>(
    val sources: List<RoutingSource<Routing, *>>,
) : RoutingSource<Routing, Any?>, Destroyable {

    constructor(vararg sources: RoutingSource<Routing, *>) : this(sources.toList())

    private val scope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined)

    override val elements: StateFlow<RoutingElements<Routing, *>> =
        combine(sources.map { it.elements }) { arr -> arr.reduce { acc, list -> acc + list } }
            .stateIn(scope, SharingStarted.Eagerly, emptyList())

    override val screenState: StateFlow<RoutingSourceAdapter.ScreenState<Routing, *>> =
        combine(sources.map { it.screenState }) { arr ->
            RoutingSourceAdapter.ScreenState(
                onScreen = arr.flatMap { it.onScreen },
                offScreen = arr.flatMap { it.offScreen },
            )
        }
            .stateIn(scope, SharingStarted.Eagerly, RoutingSourceAdapter.ScreenState())

    override val onBackPressedCallbackList: List<OnBackPressedCallback>
        get() = sources.flatMap { it.onBackPressedCallbackList }

    override fun onTransitionFinished(key: RoutingKey<Routing>) {
        sources.forEach { it.onTransitionFinished(key) }
    }

    override fun onTransitionFinished(keys: Collection<RoutingKey<Routing>>) {
        sources.forEach { it.onTransitionFinished(keys) }
    }

    override fun saveInstanceState(state: MutableSavedStateMap) {
        sources.forEach { it.saveInstanceState(state) }
    }

    override fun destroy() {
        scope.cancel()
        sources.filterIsInstance<Destroyable>().forEach { it.destroy() }
    }

}
