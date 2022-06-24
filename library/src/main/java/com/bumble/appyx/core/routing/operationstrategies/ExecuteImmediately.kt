package com.bumble.appyx.core.routing.operationstrategies

import com.bumble.appyx.core.routing.Operation

class ExecuteImmediately<Routing, State> : BaseOperationStrategy<Routing, State>() {

    override fun accept(operation: Operation<Routing, State>) {
        executeOperation(operation)
    }
}
