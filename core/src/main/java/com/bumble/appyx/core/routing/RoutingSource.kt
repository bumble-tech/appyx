package com.bumble.appyx.core.routing

import androidx.compose.runtime.Stable
import com.bumble.appyx.core.plugin.BackPressHandler
import com.bumble.appyx.core.plugin.SavesInstanceState
import com.bumble.appyx.core.plugin.UpNavigationHandler
import kotlinx.coroutines.flow.StateFlow

@Stable
interface RoutingSource<Routing, State> : RoutingSourceAdapter<Routing, State>,
    UpNavigationHandler,
    SavesInstanceState,
    BackPressHandler {

    val elements: StateFlow<RoutingElements<Routing, out State>>

    fun onTransitionFinished(key: RoutingKey<Routing>) {
        onTransitionFinished(listOf(key))
    }

    fun onTransitionFinished(keys: Collection<RoutingKey<Routing>>)

    fun accept(operation: Operation<Routing, State>) = Unit

    override fun handleUpNavigation(): Boolean =
        handleOnBackPressed()

}
