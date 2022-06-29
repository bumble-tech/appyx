package com.bumble.appyx.core.routing.operationstrategies

import com.bumble.appyx.core.routing.Operation
import com.bumble.appyx.core.routing.RoutingSource
import kotlinx.coroutines.CoroutineScope

abstract class BaseOperationStrategy<Routing, State> : OperationStrategy<Routing, State> {

    protected lateinit var scope: CoroutineScope
    protected lateinit var routingSource: RoutingSource<Routing, State>
    protected lateinit var executeOperation: (operation: Operation<Routing, State>) -> Unit

    override fun init(
        routingSource: RoutingSource<Routing, State>,
        scope: CoroutineScope,
        executeOperation: (operation: Operation<Routing, State>) -> Unit
    ) {
        this.scope = scope
        this.routingSource = routingSource
        this.executeOperation = executeOperation
    }
}
