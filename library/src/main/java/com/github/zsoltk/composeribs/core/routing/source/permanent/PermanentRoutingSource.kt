package com.github.zsoltk.composeribs.core.routing.source.permanent

import com.github.zsoltk.composeribs.core.routing.RoutingElement
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.state.SavedStateMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class PermanentRoutingSource<Key>(
    configuration: Set<Key> = emptySet(),
    savedStateMap: SavedStateMap?,
    private val key: String = PermanentRoutingSource::class.simpleName!!,
) : RoutingSource<Key, Unit> {

    constructor(
        savedStateMap: SavedStateMap?,
        vararg configuration: Key,
    ) : this(
        configuration = configuration.toSet(),
        savedStateMap = savedStateMap,
    )

    private val state = MutableStateFlow(
        savedStateMap.restore() ?: configuration.map { key ->
            RoutingElement(
                key = RoutingKey(routing = key),
                fromState = Unit,
                targetState = Unit,
            )
        }
    )

    override val all: StateFlow<List<RoutingElement<Key, Unit>>>
        get() = state

    override val onScreen: StateFlow<List<RoutingElement<Key, Unit>>>
        get() = state

    override val offScreen: StateFlow<List<RoutingElement<Key, Unit>>> =
        MutableStateFlow(emptyList())

    override val canHandleBackPress: StateFlow<Boolean> =
        MutableStateFlow(false)

    override fun onBackPressed() {
        // no-op
    }

    override fun onTransitionFinished(key: RoutingKey<Key>) {
        // no-op
    }

    fun add(key: RoutingKey<Key>) {
        if (state.value.any { it.key == key }) return
        state.update { list ->
            if (list.any { it.key == key }) {
                list
            } else {
                list + RoutingElement(
                    key = key,
                    fromState = Unit,
                    targetState = Unit,
                )
            }
        }
    }

    override fun saveInstanceState(savedStateMap: MutableMap<String, Any>) {
        savedStateMap[key] = state.value
    }

    private fun SavedStateMap?.restore(): List<RoutingElement<Key, Unit>>? =
        (this?.get(key) as? List<RoutingElement<Key, Unit>>)

}
