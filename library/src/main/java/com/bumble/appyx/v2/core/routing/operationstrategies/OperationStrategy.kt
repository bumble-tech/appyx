package com.bumble.appyx.v2.core.routing.operationstrategies

import com.bumble.appyx.v2.core.routing.Operation
import com.bumble.appyx.v2.core.routing.RoutingSource
import kotlinx.coroutines.CoroutineScope

interface OperationStrategy<Routing, State> {

    fun init(
        routingSource: RoutingSource<Routing, State>,
        scope: CoroutineScope,
        executeOperation: (operation: Operation<Routing, State>) -> Unit
    )

    fun accept(operation: Operation<Routing, State>)
}
