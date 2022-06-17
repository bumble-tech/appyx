package com.bumble.appyx.core.routing.source.spotlight.backpresshandler

import com.bumble.appyx.core.routing.backpresshandlerstrategies.BaseBackPressHandlerStrategy
import com.bumble.appyx.core.routing.source.spotlight.Spotlight
import com.bumble.appyx.core.routing.source.spotlight.Spotlight.TransitionState.ACTIVE
import com.bumble.appyx.core.routing.source.spotlight.SpotlightElements
import com.bumble.appyx.core.routing.source.spotlight.operation.Activate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UndoHistory<Routing : Any>(
    private val historyLimit: Int = 10
) : BaseBackPressHandlerStrategy<Routing, Spotlight.TransitionState>() {

    private val history = ArrayDeque<Int>()

    override val canHandleBackPressFlow: Flow<Boolean> by lazy {
        routingSource.elements.map { elements ->
            elements.addToHistory()
            historyHasElements()
        }
    }

    override fun onBackPressed() {
        history.removeLast()
        routingSource.accept(Activate(history.last()))
    }

    private fun historyHasElements() =
        history.size > 1

    private fun SpotlightElements<Routing>.addToHistory() {
        val newIndex = indexOfFirst { it.targetState == ACTIVE }
        if (newIndex != history.lastOrNull()) {
            history.addLast(newIndex)
            adjustToHistoryLimit()
        }
    }

    private fun adjustToHistoryLimit() {
        if (history.size > historyLimit) history.removeFirst()
    }
}
