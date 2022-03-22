package com.bumble.appyx.v2.core.routing.operationstrategies

import com.bumble.appyx.v2.core.routing.Operation
import com.bumble.appyx.v2.core.routing.RoutingElements
import com.bumble.appyx.v2.core.routing.RoutingSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class QueueOperations<Routing, State> : BaseOperationStrategy<Routing, State>() {

    private val operationQueue = LinkedList<Operation<Routing, State>>()

    override fun init(
        routingSource: RoutingSource<Routing, State>,
        scope: CoroutineScope,
        executeOperation: (operation: Operation<Routing, State>) -> Unit
    ) {
        super.init(routingSource, scope, executeOperation)
        collectElements()
    }

    override fun accept(operation: Operation<Routing, State>) {
        if (hasUnfinishedOperation()) {
            operationQueue.addFirst(operation)
        } else {
            executeOperation(operation)
        }
    }

    private fun collectElements() {
        scope.launch {
            routingSource
                .elements
                .collect(::addToQueueIfTransitionInProgress)
        }
    }

    private fun addToQueueIfTransitionInProgress(transitionList: RoutingElements<Routing, out State>?) {
        if (transitionList?.any { it.fromState != it.targetState } == true && operationQueue.isNotEmpty()) {
            val operation = operationQueue.removeLast()
            executeOperation(operation)
        }
    }

    private fun hasUnfinishedOperation(): Boolean {
        return routingSource.elements.value.any { it.targetState != it.fromState }
    }
}
