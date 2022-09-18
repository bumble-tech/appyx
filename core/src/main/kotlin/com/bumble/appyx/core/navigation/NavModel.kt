package com.bumble.appyx.core.navigation

import androidx.compose.runtime.Stable
import com.bumble.appyx.core.plugin.BackPressHandler
import com.bumble.appyx.core.plugin.SavesInstanceState
import com.bumble.appyx.core.plugin.UpNavigationHandler
import kotlinx.coroutines.flow.StateFlow

@Stable
interface NavModel<Routing, State> : NavModelAdapter<Routing, State>,
    UpNavigationHandler,
    SavesInstanceState,
    BackPressHandler {

    val elements: StateFlow<RoutingElements<Routing, out State>>

    fun onTransitionFinished(key: NavKey<Routing>) {
        onTransitionFinished(listOf(key))
    }

    fun onTransitionFinished(keys: Collection<NavKey<Routing>>)

    fun accept(operation: Operation<Routing, State>) = Unit

    override fun handleUpNavigation(): Boolean =
        handleOnBackPressed()

}
