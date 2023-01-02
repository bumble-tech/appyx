package com.bumble.appyx.navmodel.spotlight.backpresshandler

import android.os.Parcelable
import com.bumble.appyx.core.navigation.backpresshandlerstrategies.BaseBackPressHandlerStrategy
import com.bumble.appyx.navmodel.spotlight.Spotlight
import com.bumble.appyx.navmodel.spotlight.SpotlightElements
import com.bumble.appyx.navmodel.spotlight.operation.Previous
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GoToPrevious<NavTarget : Parcelable> :
    BaseBackPressHandlerStrategy<NavTarget, Spotlight.State>() {

    override val canHandleBackPressFlow: Flow<Boolean> by lazy {
        navModel.elements.map(::areTherePreviousElements)
    }

    private fun areTherePreviousElements(elements: SpotlightElements<NavTarget>) =
        elements.any { it.targetState == Spotlight.State.INACTIVE_BEFORE }

    override fun onBackPressed() {
        navModel.accept(Previous())
    }
}
