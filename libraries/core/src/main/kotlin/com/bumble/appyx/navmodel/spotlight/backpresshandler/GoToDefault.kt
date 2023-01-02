package com.bumble.appyx.navmodel.spotlight.backpresshandler

import android.os.Parcelable
import com.bumble.appyx.core.navigation.backpresshandlerstrategies.BaseBackPressHandlerStrategy
import com.bumble.appyx.navmodel.spotlight.Spotlight
import com.bumble.appyx.navmodel.spotlight.Spotlight.State.ACTIVE
import com.bumble.appyx.navmodel.spotlight.SpotlightElements
import com.bumble.appyx.navmodel.spotlight.operation.Activate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GoToDefault<NavTarget : Parcelable>(
    private val defaultElementIndex: Int = 0
) : BaseBackPressHandlerStrategy<NavTarget, Spotlight.State>() {

    override val canHandleBackPressFlow: Flow<Boolean> by lazy {
        navModel.elements.map(::defaultElementIsNotActive)
    }

    override fun onBackPressed() {
        navModel.accept(Activate(defaultElementIndex))
    }

    private fun defaultElementIsNotActive(elements: SpotlightElements<NavTarget>) =
        elements.getOrNull(defaultElementIndex)?.targetState != ACTIVE
}
