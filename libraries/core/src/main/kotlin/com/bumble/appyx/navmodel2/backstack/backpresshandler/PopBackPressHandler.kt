package com.bumble.appyx.navmodel2.backstack.backpresshandler

import com.bumble.appyx.core.navigation.backpresshandlerstrategies.BaseBackPressHandlerStrategy
import com.bumble.appyx.navmodel2.backstack.BackStack
import com.bumble.appyx.navmodel2.backstack.BackStackElements
import com.bumble.appyx.navmodel2.backstack.operation.Pop
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PopBackPressHandler<NavTarget : Any> :
    BaseBackPressHandlerStrategy<NavTarget, BackStack.State>() {

    override val canHandleBackPressFlow: Flow<Boolean> by lazy {
        navModel.elements.map(::areThereStashedElements)
    }

    private fun areThereStashedElements(elements: BackStackElements<NavTarget>) =
        elements.any { it.targetState == BackStack.State.STASHED }

    override fun onBackPressed() {
        navModel.accept(Pop())
    }
}
