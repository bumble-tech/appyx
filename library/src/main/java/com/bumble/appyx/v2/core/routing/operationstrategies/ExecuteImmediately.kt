package com.bumble.appyx.v2.core.routing.operationstrategies

import com.bumble.appyx.v2.core.routing.Operation

class ExecuteImmediately<Routing, State> : BaseOperationStrategy<Routing, State>() {

    override fun accept(operation: Operation<Routing, State>) {
        executeOperation(operation)
    }
}
