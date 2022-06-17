package com.bumble.appyx.core.routing.operationstrategies

import com.bumble.appyx.core.routing.Operation
import com.bumble.appyx.core.routing.isTransitioning

class IgnoreIfThereAreUnfinishedTransition<Routing, State> : BaseOperationStrategy<Routing, State>() {

    override fun accept(operation: Operation<Routing, State>) {
        if (hasNoUnfinishedTransactions()) {
            executeOperation(operation)
        }
    }

    private fun hasNoUnfinishedTransactions(): Boolean =
        routingSource.elements.value
            .none { it.isTransitioning }
}
