package com.github.zsoltk.composeribs.core.routing.source.permanent

import com.github.zsoltk.composeribs.core.state.SavedStateMap
import com.github.zsoltk.composeribs.core.routing.Operation
import com.github.zsoltk.composeribs.core.routing.RoutingElement
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class PermanentRoutingSource<Key : Any>(
    configuration: Set<Key> = emptySet(),
    savedStateMap: SavedStateMap?,
    private val key: String = PermanentRoutingSource::class.simpleName!!,
) : RoutingSource<Key, Int> {

    constructor(
        savedStateMap: SavedStateMap?,
        vararg configuration: Key,
    ) : this(
        configuration = configuration.toSet(),
        savedStateMap = savedStateMap,
    )

    private val state = MutableStateFlow(
        savedStateMap.restore() ?: configuration.map { key ->
            PermanentElement(
                key = RoutingKey(routing = key),
                fromState = 0,
                targetState = 0,
                operation = Operation.Noop()
            )
        }
    )

    override val all: StateFlow<PermanentElements<Key>>
        get() = state

    override val onScreen: StateFlow<PermanentElements<Key>>
        get() = state

    override val offScreen: StateFlow<PermanentElements<Key>> =
        MutableStateFlow(emptyList())

    override val canHandleBackPress: StateFlow<Boolean> =
        MutableStateFlow(false)

    override fun onBackPressed() {
        // no-op
    }

    override fun onTransitionFinished(key: RoutingKey<Key>) {
        // no-op
    }

    fun perform(operation: PermanentOperation<Key>) {
        if (operation.isApplicable(state.value)) {
            state.update { operation(it) }
        }
    }

    override fun saveInstanceState(savedStateMap: MutableMap<String, Any>) {
        savedStateMap[key] = state.value
    }

    private fun SavedStateMap?.restore(): List<RoutingElement<Key, Int>>? =
        (this?.get(key) as? PermanentElements<Key>)

}
