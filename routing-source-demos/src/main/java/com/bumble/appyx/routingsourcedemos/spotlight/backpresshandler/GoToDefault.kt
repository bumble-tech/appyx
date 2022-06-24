package com.bumble.appyx.routingsourcedemos.spotlight.backpresshandler

import com.bumble.appyx.core.routing.backpresshandlerstrategies.BaseBackPressHandlerStrategy
import com.bumble.appyx.routingsourcedemos.spotlight.Spotlight
import com.bumble.appyx.routingsourcedemos.spotlight.Spotlight.TransitionState.ACTIVE
import com.bumble.appyx.routingsourcedemos.spotlight.SpotlightElements
import com.bumble.appyx.routingsourcedemos.spotlight.operation.Activate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GoToDefault<Routing : Any>(
    private val defaultElementIndex: Int = 0
) : BaseBackPressHandlerStrategy<Routing, Spotlight.TransitionState>() {

    override val canHandleBackPressFlow: Flow<Boolean> by lazy {
        routingSource.elements.map(::defaultElementIsNotActive)
    }

    override fun onBackPressed() {
        routingSource.accept(Activate(defaultElementIndex))
    }

    private fun defaultElementIsNotActive(elements: SpotlightElements<Routing>) =
        elements.getOrNull(defaultElementIndex)?.targetState != ACTIVE
}
