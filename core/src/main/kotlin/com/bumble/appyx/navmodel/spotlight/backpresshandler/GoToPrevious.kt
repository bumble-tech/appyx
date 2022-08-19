package com.bumble.appyx.navmodel.spotlight.backpresshandler

import com.bumble.appyx.core.navigation.backpresshandlerstrategies.BaseBackPressHandlerStrategy
import com.bumble.appyx.navmodel.spotlight.Spotlight
import com.bumble.appyx.navmodel.spotlight.SpotlightElements
import com.bumble.appyx.navmodel.spotlight.operation.Previous
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GoToPrevious<Routing : Any> :
    BaseBackPressHandlerStrategy<Routing, Spotlight.TransitionState>() {

    override val canHandleBackPressFlow: Flow<Boolean> by lazy {
        navModel.elements.map(::areTherePreviousElements)
    }

    private fun areTherePreviousElements(elements: SpotlightElements<Routing>) =
        elements.any { it.targetState == Spotlight.TransitionState.INACTIVE_BEFORE }

    override fun onBackPressed() {
        navModel.accept(Previous())
    }
}
