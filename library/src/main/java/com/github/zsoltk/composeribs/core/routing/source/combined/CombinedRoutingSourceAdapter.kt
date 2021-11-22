package com.github.zsoltk.composeribs.core.routing.source.combined

import com.github.zsoltk.composeribs.core.routing.RoutingElements
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.routing.RoutingSourceAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class CombinedRoutingSourceAdapter<Key, State>(
    scope: CoroutineScope,
    private val sources: List<RoutingSource<Key, out State>>
) : RoutingSourceAdapter<Key, State> {

    override val elements: StateFlow<RoutingElements<Key, out State>> =
        combine(sources.map { it.elements }) { arr -> arr.reduce { acc, list -> acc + list } }
            .stateIn(scope, SharingStarted.Eagerly, emptyList())

    override val onScreen: StateFlow<RoutingElements<Key, out State>> =
        combine(sources.map { it.adapter.onScreen }) { arr -> arr.reduce { acc, list -> acc + list } }
            .stateIn(scope, SharingStarted.Eagerly, emptyList())

    override val offScreen: StateFlow<RoutingElements<Key, out State>> =
        combine(sources.map { it.adapter.offScreen }) { arr -> arr.reduce { acc, list -> acc + list } }
            .stateIn(scope, SharingStarted.Eagerly, emptyList())

    override fun onTransitionFinished(key: RoutingKey<Key>) {
        sources.forEach { it.onTransitionFinished(key) }
    }

    override fun isElementOnScreen(key: RoutingKey<Key>): Boolean =
        onScreen.value.find { it.key == key } != null
}
