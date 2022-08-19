package com.bumble.appyx.core.navigation.operationstrategies

import com.bumble.appyx.core.navigation.Operation
import com.bumble.appyx.core.navigation.isTransitioning


class FinishTransitionsOnNewOperation<Routing, State> : BaseOperationStrategy<Routing, State>() {

    override fun accept(operation: Operation<Routing, State>) {
        finishUnfinishedTransitions()
        executeOperation(operation)
    }

    private fun finishUnfinishedTransitions() {
        navModel
            .elements
            .value
            .mapNotNull { routingElement ->
                if (routingElement.isTransitioning) routingElement.key else null
            }
            .also { if (it.isNotEmpty()) navModel.onTransitionFinished(it) }
    }
}
