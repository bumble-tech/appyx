package com.github.zsoltk.composeribs.core.routing.source.combined

import com.github.zsoltk.composeribs.core.routing.RoutingElement
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlin.coroutines.EmptyCoroutineContext

class CombinedRoutingSource<Key>(
    val sources: List<RoutingSource<Key, *>>,
) : RoutingSource<Key, Any?> {

    constructor(vararg sources: RoutingSource<Key, *>) : this(sources.toList())

    // Lifecycles of this source and dependent ones are aligned, gc should be able to collect them
    // Eagerly subscription to avoid recomposition with default value
    private val scope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined)

    override val all: StateFlow<List<RoutingElement<Key, *>>> =
        combine(sources.map { it.all }) { arr -> arr.reduce { acc, list -> acc + list } }
            .stateIn(scope, SharingStarted.Eagerly, emptyList())

    override val onScreen: StateFlow<List<RoutingElement<Key, *>>> =
        combine(sources.map { it.onScreen }) { arr -> arr.reduce { acc, list -> acc + list } }
            .stateIn(scope, SharingStarted.Eagerly, emptyList())

    override val offScreen: StateFlow<List<RoutingElement<Key, *>>> =
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

    override fun isOnScreen(key: RoutingKey<Key>): Boolean =
        sources.any { it.isOnScreen(key) }

}
