package com.github.zsoltk.composeribs.core.routing

import com.github.zsoltk.composeribs.core.plugin.BackPressHandler
import com.github.zsoltk.composeribs.core.plugin.SavesInstanceState
import com.github.zsoltk.composeribs.core.plugin.UpNavigationHandler
import kotlinx.coroutines.flow.StateFlow

interface RoutingSource<Routing, State> : UpNavigationHandler, SavesInstanceState,
    BackPressHandler, RoutingSourceAdapter<Routing, State> {

    val elements: StateFlow<RoutingElements<Routing, out State>>

    val canHandleBackPress: StateFlow<Boolean>

    fun onTransitionFinished(key: RoutingKey<Routing>)

    fun accept(operation: Operation<Routing, State>) = Unit

    override fun handleUpNavigation(): Boolean =
        canHandleBackPress.value.also { if (it) onBackPressed() }
}
