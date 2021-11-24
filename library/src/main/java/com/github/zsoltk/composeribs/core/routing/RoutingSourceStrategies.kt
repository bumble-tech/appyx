package com.github.zsoltk.composeribs.core.routing

import androidx.annotation.CheckResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.LinkedList

class QueueTransitionsRoutingSource<Key, State>(
    coroutineScope: CoroutineScope,
    private val routingSource: RoutingSource<Key, State>
) : RoutingSource<Key, State> by routingSource {

    private val operationQueue = LinkedList<Operation<Key, State>>()

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

    override fun accept(operation: Operation<Key, State>) {
        if (checkUnfinishedOperation()) {
            operationQueue.addFirst(operation)
        } else {
            routingSource.accept(operation)
        }
    }
}

class FinishTransitionsOnNewOneRoutingSource<Key, State>(
    private val routingSource: RoutingSource<Key, State>
) : RoutingSource<Key, State> by routingSource {

    override fun accept(operation: Operation<Key, State>) {
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

class RejectIfHasUnfinishedTransitionsRoutingSource<Key, State>(
    private val routingSource: RoutingSource<Key, State>
) : RoutingSource<Key, State> by routingSource {

    override fun accept(operation: Operation<Key, State>) {
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
fun <Key, State> RoutingSource<Key, State>.wrapWithQueueBehaviour(coroutineScope: CoroutineScope)
        : RoutingSource<Key, State> = QueueTransitionsRoutingSource(coroutineScope, this)

@CheckResult
fun <Key, State> RoutingSource<Key, State>.wrapWithFinishBehaviour(): RoutingSource<Key, State> =
    FinishTransitionsOnNewOneRoutingSource(this)

@CheckResult
fun <Key, State> RoutingSource<Key, State>.wrapWithRejectBehaviour(): RoutingSource<Key, State> =
    RejectIfHasUnfinishedTransitionsRoutingSource(this)

