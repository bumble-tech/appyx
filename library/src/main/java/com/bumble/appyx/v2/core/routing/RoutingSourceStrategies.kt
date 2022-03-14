package com.bumble.appyx.v2.core.routing

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class QueueTransitionsRoutingSource<Routing, State> : OperationStrategy<Routing, State> {

    private val operationQueue = LinkedList<Operation<Routing, State>>()
    private lateinit var scope: CoroutineScope
    private lateinit var routingSource: BaseRoutingSource<Routing, State>

    override fun init(routingSource: BaseRoutingSource<Routing, State>, scope: CoroutineScope) {
        this.scope = scope
        this.routingSource = routingSource
        collectElements()
    }

    override fun accept(operation: Operation<Routing, State>) {
        if (checkUnfinishedOperation()) {
            operationQueue.addFirst(operation)
        } else {
            routingSource.execute(operation)
        }
    }

    private fun collectElements() {
        scope.launch {
            routingSource
                .elements
                .collect { transactionList ->
                    if (!transactionList.any { it.fromState != it.targetState } && operationQueue.isNotEmpty()) {
                        val command = operationQueue.removeLast()
                        routingSource.execute(command)
                    }
                }
        }
    }

    private fun checkUnfinishedOperation(): Boolean {
        return routingSource.elements.value.any { it.targetState != it.fromState }
    }
}

class FinishTransitionsOnNewOneRoutingSource<Routing, State>
    : OperationStrategy<Routing, State> {

    private lateinit var routingSource: BaseRoutingSource<Routing, State>

    override fun init(routingSource: BaseRoutingSource<Routing, State>, scope: CoroutineScope) {
        this.routingSource = routingSource
    }

    override fun accept(operation: Operation<Routing, State>) {
        finishUnfinishedTransitions()
        routingSource.execute(operation)
    }

    private fun finishUnfinishedTransitions() {
        routingSource.elements.value.forEach { routingElement ->
            if (routingElement.fromState != routingElement.targetState) {
                routingSource.onTransitionFinished(routingElement.key)
            }
        }
    }
}

class RejectIfHasUnfinishedTransitionsRoutingSource<Routing, State>
    : OperationStrategy<Routing, State> {

    private lateinit var routingSource: BaseRoutingSource<Routing, State>

    override fun init(routingSource: BaseRoutingSource<Routing, State>, scope: CoroutineScope) {
        this.routingSource = routingSource
    }

    override fun accept(operation: Operation<Routing, State>) {
        if (hasNotUnfinishedTransactions()) {
            routingSource.execute(operation)
        }
    }

    private fun hasNotUnfinishedTransactions(): Boolean =
        routingSource.elements.value
            .any { it.fromState != it.targetState }
            .not()
}
