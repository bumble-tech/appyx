package com.bumble.appyx.navmodel.backstack.backpresshandler

import com.bumble.appyx.core.navigation.backpresshandlerstrategies.BaseBackPressHandlerStrategy
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.BackStackElements
import com.bumble.appyx.navmodel.backstack.operation.Pop
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PopBackPressHandler<Routing : Any> :
    BaseBackPressHandlerStrategy<Routing, BackStack.TransitionState>() {

    override val canHandleBackPressFlow: Flow<Boolean> by lazy {
        navModel.elements.map(::areThereStashedElements)
    }

    private fun areThereStashedElements(elements: BackStackElements<Routing>) =
        elements.any { it.targetState == BackStack.TransitionState.STASHED_IN_BACK_STACK }

    override fun onBackPressed() {
        navModel.accept(Pop())
    }
}
