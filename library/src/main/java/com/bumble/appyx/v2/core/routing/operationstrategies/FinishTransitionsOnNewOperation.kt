package com.bumble.appyx.v2.core.routing.operationstrategies

import com.bumble.appyx.v2.core.routing.Operation


class FinishTransitionsOnNewOperation<Routing, State> : BaseOperationStrategy<Routing, State>() {

    override fun accept(operation: Operation<Routing, State>) {
        finishUnfinishedTransitions()
        executeOperation(operation)
    }

    private fun finishUnfinishedTransitions() {
        routingSource.elements.value.forEach { routingElement ->
            if (routingElement.fromState != routingElement.targetState) {
                routingSource.onTransitionFinished(routingElement.key)
            }
        }
    }
}
