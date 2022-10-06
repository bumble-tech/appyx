package com.bumble.appyx.navmodel2.backstack.backpresshandler

import com.bumble.appyx.core.navigation2.NavElements
import com.bumble.appyx.core.navigation2.backpresshandlerstrategies.BaseBackPressHandlerStrategy
import com.bumble.appyx.navmodel2.backstack.BackStack.State
import com.bumble.appyx.navmodel2.backstack.operation.Pop
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PopBackPressHandler<NavTarget : Any> :
    BaseBackPressHandlerStrategy<NavTarget, State>() {

    override val canHandleBackPressFlow: Flow<Boolean> by lazy {
        navModel.elements.map {
            areThereStashedElements(
                it.navTransition.targetState
            )
        }
    }

    private fun areThereStashedElements(elements: NavElements<NavTarget, State>): Boolean =
        elements.any { it.state == State.STASHED }

    override fun onBackPressed() {
        // FIXME this would need to talk to an InputSource instead to get a transition too
        navModel.enqueue(Pop())
    }
}
