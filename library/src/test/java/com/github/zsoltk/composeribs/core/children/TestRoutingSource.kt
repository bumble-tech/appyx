package com.github.zsoltk.composeribs.core.children

import com.github.zsoltk.composeribs.core.routing.RoutingElement
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class TestRoutingSource<Key> : RoutingSource<Key, Int> {

    data class RoutingKeyImpl<Key>(
        override val routing: Key,
    ) : RoutingKey<Key>

    private val state = MutableStateFlow(emptyList<RoutingElement<Key, Int>>())
    override val all: StateFlow<List<RoutingElement<Key, Int>>>
        get() = state
    override val onScreen: StateFlow<List<RoutingElement<Key, Int>>>
        get() = all
    override val offScreen: StateFlow<List<RoutingElement<Key, Int>>>
        get() = MutableStateFlow(emptyList())
    override val canHandleBackPress: StateFlow<Boolean>
        get() = MutableStateFlow(false)

    fun add(key: Key) {
        state.update { list ->
            require(list.none { it.key.routing == key })
            list + RoutingElement(key = RoutingKeyImpl(key), fromState = 0, targetState = 0)
        }
    }

    override fun onBackPressed() {
    }

    override fun onTransitionFinished(key: RoutingKey<Key>) {
    }

}
