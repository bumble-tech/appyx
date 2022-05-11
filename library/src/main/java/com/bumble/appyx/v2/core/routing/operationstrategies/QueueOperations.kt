package com.bumble.appyx.v2.core.routing.operationstrategies

import com.bumble.appyx.v2.core.routing.Operation
import com.bumble.appyx.v2.core.routing.RoutingElements
import com.bumble.appyx.v2.core.routing.isTransitioning
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class QueueOperations<Routing, State> : BaseOperationStrategy<Routing, State>() {

    private val operationQueue = LinkedList<Operation<Routing, State>>()
    private lateinit var collectJob: Job

    override fun accept(operation: Operation<Routing, State>) {
        collectElementsIfRequired()

        if (hasUnfinishedOperation()) {
            operationQueue.addFirst(operation)
        } else {
            executeOperation(operation)
        }
    }

    private fun collectElementsIfRequired() {
        if (::collectJob.isInitialized.not()) {
            collectJob = scope.launch {
                routingSource
                    .elements
                    .collect(::addToQueueIfTransitionInProgress)
            }
        }
    }

    private fun addToQueueIfTransitionInProgress(transitionList: RoutingElements<Routing, out State>) {
        if (transitionList.none { it.isTransitioning } && operationQueue.isNotEmpty()) {
            val operation = operationQueue.removeLast()
            executeOperation(operation)
        }
    }

    private fun hasUnfinishedOperation(): Boolean {
        return routingSource.elements.value.any { it.targetState != it.fromState }
    }
}
