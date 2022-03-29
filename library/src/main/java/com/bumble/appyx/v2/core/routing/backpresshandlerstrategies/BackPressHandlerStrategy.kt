package com.bumble.appyx.v2.core.routing.backpresshandlerstrategies

import com.bumble.appyx.v2.core.plugin.BackPressHandler
import com.bumble.appyx.v2.core.routing.BaseRoutingSource
import com.bumble.appyx.v2.core.routing.RoutingSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface BackPressHandlerStrategy<Routing, State>
    : BackPressHandler {

    fun init(routingSource: BaseRoutingSource<Routing, State>, scope: CoroutineScope)

    val canHandleBackPress: StateFlow<Boolean>

}
