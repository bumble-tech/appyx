package com.bumble.appyx.core.routing.backpresshandlerstrategies

import com.bumble.appyx.core.routing.BaseRoutingSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface BackPressHandlerStrategy<Routing, State> {

    fun init(routingSource: BaseRoutingSource<Routing, State>, scope: CoroutineScope)

    val canHandleBackPress: StateFlow<Boolean>

    fun onBackPressed()

}
