package com.github.zsoltk.composeribs.core.routing.source.permanent

import com.github.zsoltk.composeribs.core.routing.AlwaysOnScreenResolver
import com.github.zsoltk.composeribs.core.routing.Operation
import com.github.zsoltk.composeribs.core.routing.RoutingElement
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.routing.RoutingSourceAdapter
import com.github.zsoltk.composeribs.core.routing.adapter
import com.github.zsoltk.composeribs.core.state.SavedStateMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.coroutines.EmptyCoroutineContext

class PermanentRoutingSource<Key : Any>(
    configuration: Set<Key> = emptySet(),
    savedStateMap: SavedStateMap?,
    private val key: String = PermanentRoutingSource::class.simpleName!!,
) : RoutingSource<Key, Int> {

    private val scope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined)

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

    override val adapter: RoutingSourceAdapter<Key, Int> =
        adapter(scope, AlwaysOnScreenResolver())

    override val elements: StateFlow<PermanentElements<Key>>
        get() = state

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
