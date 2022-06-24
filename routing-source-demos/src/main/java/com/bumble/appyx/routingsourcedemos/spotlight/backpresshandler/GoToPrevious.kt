package com.bumble.appyx.routingsourcedemos.spotlight.backpresshandler

import com.bumble.appyx.core.routing.backpresshandlerstrategies.BaseBackPressHandlerStrategy
import com.bumble.appyx.routingsourcedemos.spotlight.Spotlight
import com.bumble.appyx.routingsourcedemos.spotlight.SpotlightElements
import com.bumble.appyx.routingsourcedemos.spotlight.operation.Previous
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GoToPrevious<Routing : Any> :
    BaseBackPressHandlerStrategy<Routing, Spotlight.TransitionState>() {

    override val canHandleBackPressFlow: Flow<Boolean> by lazy {
        routingSource.elements.map(::areTherePreviousElements)
    }

    private fun areTherePreviousElements(elements: SpotlightElements<Routing>) =
        elements.any { it.targetState == Spotlight.TransitionState.INACTIVE_BEFORE }

    override fun onBackPressed() {
        routingSource.accept(Previous())
    }
}
