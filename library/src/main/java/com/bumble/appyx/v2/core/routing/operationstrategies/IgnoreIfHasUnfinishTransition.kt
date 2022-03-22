package com.bumble.appyx.v2.core.routing.operationstrategies

import com.bumble.appyx.v2.core.routing.Operation

class IgnoreIfThereAreUnfinishedTransition<Routing, State> : BaseOperationStrategy<Routing, State>() {

    override fun accept(operation: Operation<Routing, State>) {
        if (hasNotUnfinishedTransactions()) {
            executeOperation(operation)
        }
    }

    private fun hasNotUnfinishedTransactions(): Boolean =
        routingSource.elements.value
            .any { it.fromState != it.targetState }
            .not()
}
