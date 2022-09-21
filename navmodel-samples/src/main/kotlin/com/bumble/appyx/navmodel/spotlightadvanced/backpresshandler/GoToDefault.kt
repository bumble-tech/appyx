package com.bumble.appyx.navmodel.spotlightadvanced.backpresshandler

import com.bumble.appyx.core.navigation.backpresshandlerstrategies.BaseBackPressHandlerStrategy
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.State.Active
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvancedElements
import com.bumble.appyx.navmodel.spotlightadvanced.operation.Activate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GoToDefault<NavTarget : Any>(
    private val defaultElementIndex: Int = 0
) : BaseBackPressHandlerStrategy<NavTarget, SpotlightAdvanced.State>() {

    override val canHandleBackPressFlow: Flow<Boolean> by lazy {
        navModel.elements.map(::defaultElementIsNotActive)
    }

    override fun onBackPressed() {
        navModel.accept(Activate(defaultElementIndex))
    }

    private fun defaultElementIsNotActive(elements: SpotlightAdvancedElements<NavTarget>) =
        elements.getOrNull(defaultElementIndex)?.targetState != Active
}
