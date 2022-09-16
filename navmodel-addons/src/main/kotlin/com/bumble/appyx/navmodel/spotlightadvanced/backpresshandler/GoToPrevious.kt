package com.bumble.appyx.navmodel.spotlightadvanced.backpresshandler

import com.bumble.appyx.core.navigation.backpresshandlerstrategies.BaseBackPressHandlerStrategy
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvancedElements
import com.bumble.appyx.navmodel.spotlightadvanced.operation.Previous
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GoToPrevious<Routing : Any> :
    BaseBackPressHandlerStrategy<Routing, SpotlightAdvanced.TransitionState>() {

    override val canHandleBackPressFlow: Flow<Boolean> by lazy {
        navModel.elements.map(::areTherePreviousElements)
    }

    private fun areTherePreviousElements(elements: SpotlightAdvancedElements<Routing>) =
        elements.any { it.targetState == SpotlightAdvanced.TransitionState.InactiveBefore }

    override fun onBackPressed() {
        navModel.accept(Previous())
    }
}
