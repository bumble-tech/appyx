package com.bumble.appyx.navmodel.spotlight.backpresshandler

import com.bumble.appyx.core.navigation.backpresshandlerstrategies.BaseBackPressHandlerStrategy
import com.bumble.appyx.navmodel.spotlight.Spotlight
import com.bumble.appyx.navmodel.spotlight.Spotlight.TransitionState.ACTIVE
import com.bumble.appyx.navmodel.spotlight.SpotlightElements
import com.bumble.appyx.navmodel.spotlight.operation.Activate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GoToDefault<Routing : Any>(
    private val defaultElementIndex: Int = 0
) : BaseBackPressHandlerStrategy<Routing, Spotlight.TransitionState>() {

    override val canHandleBackPressFlow: Flow<Boolean> by lazy {
        navModel.elements.map(::defaultElementIsNotActive)
    }

    override fun onBackPressed() {
        navModel.accept(Activate(defaultElementIndex))
    }

    private fun defaultElementIsNotActive(elements: SpotlightElements<Routing>) =
        elements.getOrNull(defaultElementIndex)?.targetState != ACTIVE
}
