package com.bumble.appyx.core.navigation.model.combined

import androidx.activity.OnBackPressedCallback
import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.core.navigation.NavModel
import com.bumble.appyx.core.navigation.NavModelAdapter
import com.bumble.appyx.core.plugin.Destroyable
import com.bumble.appyx.core.state.MutableSavedStateMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlin.coroutines.EmptyCoroutineContext

class CombinedNavModel<NavTarget>(
    val navModels: List<NavModel<NavTarget, *>>,
) : NavModel<NavTarget, Any?>, Destroyable {

    constructor(vararg navModels: NavModel<NavTarget, *>) : this(navModels.toList())

    private val scope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined)

    override val elements: StateFlow<NavElements<NavTarget, *>> =
        combine(navModels.map { it.elements }) { arr -> arr.reduce { acc, list -> acc + list } }
            .stateIn(scope, SharingStarted.Eagerly, emptyList())

    override val screenState: StateFlow<NavModelAdapter.ScreenState<NavTarget, *>> =
        combine(navModels.map { it.screenState }) { arr ->
            NavModelAdapter.ScreenState(
                onScreen = arr.flatMap { it.onScreen },
                offScreen = arr.flatMap { it.offScreen },
            )
        }
            .stateIn(scope, SharingStarted.Eagerly, NavModelAdapter.ScreenState())

    override val onBackPressedCallbackList: List<OnBackPressedCallback>
        get() = navModels.flatMap { it.onBackPressedCallbackList }

    override fun onTransitionFinished(key: NavKey<NavTarget>) {
        navModels.forEach { it.onTransitionFinished(key) }
    }

    override fun onTransitionFinished(keys: Collection<NavKey<NavTarget>>) {
        navModels.forEach { it.onTransitionFinished(keys) }
    }

    override fun saveInstanceState(state: MutableSavedStateMap) {
        navModels.forEach { it.saveInstanceState(state) }
    }

    override fun destroy() {
        scope.cancel()
        navModels.filterIsInstance<Destroyable>().forEach { it.destroy() }
    }

}
