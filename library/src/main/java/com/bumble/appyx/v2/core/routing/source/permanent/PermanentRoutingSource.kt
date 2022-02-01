package com.bumble.appyx.v2.core.routing.source.permanent

import com.bumble.appyx.v2.core.routing.Operation
import com.bumble.appyx.v2.core.routing.RoutingElement
import com.bumble.appyx.v2.core.routing.RoutingElements
import com.bumble.appyx.v2.core.routing.RoutingKey
import com.bumble.appyx.v2.core.routing.RoutingSource
import com.bumble.appyx.v2.core.state.SavedStateMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class PermanentRoutingSource<Routing : Any>(
    configuration: Set<Routing> = emptySet(),
    savedStateMap: SavedStateMap?,
    private val key: String,
) : RoutingSource<Routing, Int> {

    constructor(
        savedStateMap: SavedStateMap?,
        vararg configuration: Routing,
        key: String,
    ) : this(
        configuration = configuration.toSet(),
        savedStateMap = savedStateMap,
        key = key
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

    override val elements: StateFlow<PermanentElements<Routing>>
        get() = state

    override val onScreen: StateFlow<RoutingElements<Routing, Int>>
        get() = state

    override val offScreen: StateFlow<RoutingElements<Routing, Int>>
        get() = MutableStateFlow(emptyList())

    override val canHandleBackPress: StateFlow<Boolean> =
        MutableStateFlow(false)

    override fun onBackPressed() {
        // no-op
    }

    override fun onTransitionFinished(key: RoutingKey<Routing>) {
        // no-op
    }

    override fun accept(operation: Operation<Routing, Int>) {
        if (operation.isApplicable(state.value)) {
            state.update { operation(it) }
        }
    }

    override fun saveInstanceState(savedStateMap: MutableMap<String, Any>) {
        savedStateMap[key] = state.value
    }

    private fun SavedStateMap?.restore(): List<RoutingElement<Routing, Int>>? =
        (this?.get(key) as? PermanentElements<Routing>)

}
