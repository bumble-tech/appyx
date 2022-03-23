package com.bumble.appyx.v2.core.routing.operationstrategies

import com.bumble.appyx.v2.core.routing.Operation

class IgnoreIfThereAreUnfinishedTransition<Routing, State> : BaseOperationStrategy<Routing, State>() {

    override fun accept(operation: Operation<Routing, State>) {
        if (hasNoUnfinishedTransactions()) {
            executeOperation(operation)
        }
    }

    private fun hasNoUnfinishedTransactions(): Boolean =
        routingSource.elements.value
            .none { it.fromState != it.targetState }
}
