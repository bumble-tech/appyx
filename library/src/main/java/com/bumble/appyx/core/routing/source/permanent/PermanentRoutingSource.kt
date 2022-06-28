package com.bumble.appyx.core.routing.source.permanent

import com.bumble.appyx.core.routing.Operation
import com.bumble.appyx.core.routing.RoutingElement
import com.bumble.appyx.core.routing.RoutingElements
import com.bumble.appyx.core.routing.RoutingKey
import com.bumble.appyx.core.routing.RoutingSource
import com.bumble.appyx.core.routing.RoutingSourceAdapter
import com.bumble.appyx.core.state.MutableSavedStateMap
import com.bumble.appyx.core.state.SavedStateMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlin.coroutines.EmptyCoroutineContext

class PermanentRoutingSource<Routing : Any>(
    routings: Set<Routing> = emptySet(),
    savedStateMap: SavedStateMap?,
    private val key: String = requireNotNull(PermanentRoutingSource::class.qualifiedName),
) : RoutingSource<Routing, Int> {
    private val scope: CoroutineScope =
        CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined)

    constructor(
        vararg routings: Routing,
        savedStateMap: SavedStateMap?,
        key: String = requireNotNull(PermanentRoutingSource::class.qualifiedName),
    ) : this(
        routings = routings.toSet(),
        savedStateMap = savedStateMap,
        key = key
    )

    private val state = MutableStateFlow(
        savedStateMap.restore() ?: routings.map { key ->
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

    override val visibilityState: StateFlow<RoutingSourceAdapter.VisibilityState<Routing, Int>>
        get() = state
            .map { RoutingSourceAdapter.VisibilityState(onScreen = it) }
            .stateIn(
                scope = scope,
                started = SharingStarted.Lazily,
                initialValue = RoutingSourceAdapter.VisibilityState(onScreen = state.value)
            )

    override val canHandleBackPress: StateFlow<Boolean> =
        MutableStateFlow(false)

    override fun onBackPressed() {
        // no-op
    }

    override fun onTransitionFinished(keys: Collection<RoutingKey<Routing>>) {
        // no-op
    }

    override fun accept(operation: Operation<Routing, Int>) {
        if (operation.isApplicable(state.value)) {
            state.update { operation(it) }
        }
    }

    override fun saveInstanceState(state: MutableSavedStateMap) {
        state[key] = this.state.value
    }

    private fun SavedStateMap?.restore(): List<RoutingElement<Routing, Int>>? =
        (this?.get(key) as? PermanentElements<Routing>)

}
