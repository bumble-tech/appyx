package com.bumble.appyx.core.routing.operationstrategies

import com.bumble.appyx.core.routing.Operation
import com.bumble.appyx.core.routing.isTransitioning


class FinishTransitionsOnNewOperation<Routing, State> : BaseOperationStrategy<Routing, State>() {

    override fun accept(operation: Operation<Routing, State>) {
        finishUnfinishedTransitions()
        executeOperation(operation)
    }

    private fun finishUnfinishedTransitions() {
        routingSource
            .elements
            .value
            .mapNotNull { routingElement ->
                if (routingElement.isTransitioning) routingElement.key else null
            }
            .also { if (it.isNotEmpty()) routingSource.onTransitionFinished(it) }
    }
}
