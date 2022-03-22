package com.bumble.appyx.v2.core.routing.backpresshandlerstrategies

import com.bumble.appyx.v2.core.plugin.BackPressHandler
import com.bumble.appyx.v2.core.routing.RoutingSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface BackPressHandlerStrategy<Routing, State, Source : RoutingSource<Routing, State>>
    : BackPressHandler {

    fun init(routingSource: Source, scope: CoroutineScope)

    val canHandleBackPress: StateFlow<Boolean>

}
