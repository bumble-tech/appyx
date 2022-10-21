package com.bumble.appyx.core.navigation.model.permanent

import com.bumble.appyx.core.mapState
import com.bumble.appyx.core.navigation.NavElement
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.core.navigation.NavModel
import com.bumble.appyx.core.navigation.NavModelAdapter
import com.bumble.appyx.core.navigation.Operation
import com.bumble.appyx.core.state.MutableSavedStateMap
import com.bumble.appyx.core.state.SavedStateMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.coroutines.EmptyCoroutineContext

class PermanentNavModel<NavTarget : Any>(
    navTargets: Set<NavTarget> = emptySet(),
    savedStateMap: SavedStateMap?,
    private val key: String = requireNotNull(PermanentNavModel::class.qualifiedName),
) : NavModel<NavTarget, Int> {
    private val scope: CoroutineScope =
        CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined)

    constructor(
        vararg navTargets: NavTarget,
        savedStateMap: SavedStateMap?,
        key: String = requireNotNull(PermanentNavModel::class.qualifiedName),
    ) : this(
        navTargets = navTargets.toSet(),
        savedStateMap = savedStateMap,
        key = key
    )

    private val state = MutableStateFlow(
        savedStateMap.restore() ?: navTargets.map { key ->
            PermanentElement(
                key = NavKey(navTarget = key),
                fromState = 0,
                targetState = 0,
                operation = Operation.Noop()
            )
        }
    )

    override val elements: StateFlow<PermanentElements<NavTarget>>
        get() = state

    override val screenState: StateFlow<NavModelAdapter.ScreenState<NavTarget, Int>> by lazy {
        state.mapState(scope) { NavModelAdapter.ScreenState(onScreen = it) }
    }

    override fun onTransitionFinished(keys: Collection<NavKey<NavTarget>>) {
        // no-op
    }

    override fun accept(operation: Operation<NavTarget, Int>) {
        if (operation.isApplicable(state.value)) {
            state.update { operation(it) }
        }
    }

    override fun saveInstanceState(state: MutableSavedStateMap) {
        state[key] = this.state.value
    }

    private fun SavedStateMap?.restore(): List<NavElement<NavTarget, Int>>? =
        (this?.get(key) as? PermanentElements<NavTarget>)

}
