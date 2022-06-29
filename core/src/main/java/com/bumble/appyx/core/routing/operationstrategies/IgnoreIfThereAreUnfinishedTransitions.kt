package com.bumble.appyx.core.routing.operationstrategies

import com.bumble.appyx.core.routing.Operation
import com.bumble.appyx.core.routing.isTransitioning

class IgnoreIfThereAreUnfinishedTransitions<Routing, State> : BaseOperationStrategy<Routing, State>() {

    override fun accept(operation: Operation<Routing, State>) {
        if (hasNoUnfinishedTransitions()) {
            executeOperation(operation)
        }
    }

    private fun hasNoUnfinishedTransitions(): Boolean =
        routingSource.elements.value
            .none { it.isTransitioning }
}
