package com.bumble.appyx.v2.core.routing.source.combined

import com.bumble.appyx.v2.core.plugin.Destroyable
import com.bumble.appyx.v2.core.routing.RoutingElements
import com.bumble.appyx.v2.core.routing.RoutingKey
import com.bumble.appyx.v2.core.routing.RoutingSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlin.coroutines.EmptyCoroutineContext

class CombinedRoutingSource<Key>(
    val sources: List<RoutingSource<Key, *>>,
) : RoutingSource<Key, Any?>, Destroyable {

    constructor(vararg sources: RoutingSource<Key, *>) : this(sources.toList())

    private val scope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined)

    override val elements: StateFlow<RoutingElements<Key, *>> =
        combine(sources.map { it.elements }) { arr -> arr.reduce { acc, list -> acc + list } }
            .stateIn(scope, SharingStarted.Eagerly, emptyList())

    override val onScreen: StateFlow<RoutingElements<Key, *>> =
        combine(sources.map { it.onScreen }) { arr -> arr.reduce { acc, list -> acc + list } }
            .stateIn(scope, SharingStarted.Eagerly, emptyList())

    override val offScreen: StateFlow<RoutingElements<Key, *>> =
        combine(sources.map { it.offScreen }) { arr -> arr.reduce { acc, list -> acc + list } }
            .stateIn(scope, SharingStarted.Eagerly, emptyList())

    override val canHandleBackPress: StateFlow<Boolean> =
        combine(sources.map { it.canHandleBackPress }) { arr -> arr.any { it } }
            .stateIn(scope, SharingStarted.Eagerly, false)

    override fun onBackPressed() {
        sources.firstOrNull { it.canHandleBackPress.value }?.onBackPressed()
    }

    override fun onTransitionFinished(key: RoutingKey<Key>) {
        sources.forEach { it.onTransitionFinished(key) }
    }

    override fun saveInstanceState(savedStateMap: MutableMap<String, Any>) {
        sources.forEach { it.saveInstanceState(savedStateMap) }
    }

    override fun destroy() {
        scope.cancel()
        sources.filterIsInstance<Destroyable>().forEach { it.destroy() }
    }

}
