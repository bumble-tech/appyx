package com.bumble.appyx.v2.core.routing

import androidx.compose.runtime.Stable
import com.bumble.appyx.v2.core.plugin.BackPressHandler
import com.bumble.appyx.v2.core.plugin.SavesInstanceState
import com.bumble.appyx.v2.core.plugin.UpNavigationHandler
import kotlinx.coroutines.flow.StateFlow

@Stable
interface RoutingSource<Routing, State> : RoutingSourceAdapter<Routing, State>,
    UpNavigationHandler,
    SavesInstanceState,
    BackPressHandler {

    val elements: StateFlow<RoutingElements<Routing, out State>>

    val canHandleBackPress: StateFlow<Boolean>

    fun onTransitionFinished(key: RoutingKey<Routing>) {
        onTransitionFinished(listOf(key))
    }

    fun onTransitionFinished(keys: Collection<RoutingKey<Routing>>)

    fun accept(operation: Operation<Routing, State>) = Unit

    override fun handleUpNavigation(): Boolean =
        canHandleBackPress.value.also { if (it) onBackPressed() }
}
