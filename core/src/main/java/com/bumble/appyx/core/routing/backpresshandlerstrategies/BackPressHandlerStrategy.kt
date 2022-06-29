package com.bumble.appyx.core.routing.backpresshandlerstrategies

import com.bumble.appyx.core.plugin.BackPressHandler
import com.bumble.appyx.core.routing.BaseRoutingSource
import com.bumble.appyx.core.routing.RoutingSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface BackPressHandlerStrategy<Routing, State>
    : BackPressHandler {

    fun init(routingSource: BaseRoutingSource<Routing, State>, scope: CoroutineScope)

    val canHandleBackPress: StateFlow<Boolean>

}
