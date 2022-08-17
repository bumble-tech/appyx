package com.bumble.appyx.core.navigation.operationstrategies

import com.bumble.appyx.core.navigation.Operation

class ExecuteImmediately<Routing, State> : BaseOperationStrategy<Routing, State>() {

    override fun accept(operation: Operation<Routing, State>) {
        executeOperation(operation)
    }
}
