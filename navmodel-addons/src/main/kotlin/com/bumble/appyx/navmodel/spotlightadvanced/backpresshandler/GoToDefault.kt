package com.bumble.appyx.navmodel.spotlightadvanced.backpresshandler

import com.bumble.appyx.core.navigation.backpresshandlerstrategies.BaseBackPressHandlerStrategy
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.TransitionState.Active
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvancedElements
import com.bumble.appyx.navmodel.spotlightadvanced.operation.Activate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GoToDefault<Routing : Any>(
    private val defaultElementIndex: Int = 0
) : BaseBackPressHandlerStrategy<Routing, SpotlightAdvanced.TransitionState>() {

    override val canHandleBackPressFlow: Flow<Boolean> by lazy {
        navModel.elements.map(::defaultElementIsNotActive)
    }

    override fun onBackPressed() {
        navModel.accept(Activate(defaultElementIndex))
    }

    private fun defaultElementIsNotActive(elements: SpotlightAdvancedElements<Routing>) =
        elements.getOrNull(defaultElementIndex)?.targetState != Active
}
