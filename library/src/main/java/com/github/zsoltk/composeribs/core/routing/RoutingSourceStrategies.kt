package com.github.zsoltk.composeribs.core.routing

import androidx.annotation.CheckResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.LinkedList

class QueueTransitionsRoutingSource<Routing, State>(
    coroutineScope: CoroutineScope,
    private val routingSource: RoutingSource<Routing, State>
) : RoutingSource<Routing, State> by routingSource {

    private val operationQueue = LinkedList<Operation<Routing, State>>()

    init {
        coroutineScope.launch {
            routingSource
                .elements
                .collect { transactionList ->
                    if (!transactionList.any { it.fromState != it.targetState } && operationQueue.isNotEmpty()) {
                        val command = operationQueue.removeLast()
                        routingSource.accept(command)
                    }
                }
        }
    }

    private fun checkUnfinishedOperation(): Boolean {
        return routingSource.elements.value.any { it.targetState != it.fromState }
    }

    override fun accept(operation: Operation<Routing, State>) {
        if (checkUnfinishedOperation()) {
            operationQueue.addFirst(operation)
        } else {
            routingSource.accept(operation)
        }
    }
}

class FinishTransitionsOnNewOneRoutingSource<Routing, State>(
    private val routingSource: RoutingSource<Routing, State>
) : RoutingSource<Routing, State> by routingSource {

    override fun accept(operation: Operation<Routing, State>) {
        finishUnfinishedTransitions()
        routingSource.accept(operation)
    }

    private fun finishUnfinishedTransitions() {
        routingSource.elements.value.forEach { routingElement ->
            if (routingElement.fromState != routingElement.targetState) {
                onTransitionFinished(routingElement.key)
            }
        }
    }
}

class RejectIfHasUnfinishedTransitionsRoutingSource<Routing, State>(
    private val routingSource: RoutingSource<Routing, State>
) : RoutingSource<Routing, State> by routingSource {

    override fun accept(operation: Operation<Routing, State>) {
        if (hasNotUnfinishedTransactions()) {
            routingSource.accept(operation)
        }
    }

    private fun hasNotUnfinishedTransactions(): Boolean =
        elements.value
            .any { it.fromState != it.targetState }
            .not()
}

@CheckResult
fun <Routing, State> RoutingSource<Routing, State>.wrapWithQueueBehaviour(coroutineScope: CoroutineScope)
        : RoutingSource<Routing, State> = QueueTransitionsRoutingSource(coroutineScope, this)

@CheckResult
fun <Routing, State> RoutingSource<Routing, State>.wrapWithFinishBehaviour(): RoutingSource<Routing, State> =
    FinishTransitionsOnNewOneRoutingSource(this)

@CheckResult
fun <Routing, State> RoutingSource<Routing, State>.wrapWithRejectBehaviour(): RoutingSource<Routing, State> =
    RejectIfHasUnfinishedTransitionsRoutingSource(this)

