package com.bumble.appyx.v2.core.routing.source.backstack.backpresshandler

import com.bumble.appyx.v2.core.routing.backpresshandlerstrategies.BaseBackPressHandlerStrategy
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack
import com.bumble.appyx.v2.core.routing.source.backstack.BackStackElements
import com.bumble.appyx.v2.core.routing.source.backstack.operation.pop
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PopBackPressHandler<Routing : Any> :
    BaseBackPressHandlerStrategy<Routing, BackStack.TransitionState, BackStack<Routing>>() {

    override val canHandleBackPressFlow: Flow<Boolean> by lazy {
        routingSource.elements.map(::areThereStashedElements)
    }

    private fun areThereStashedElements(elements: BackStackElements<Routing>) =
        elements.any { it.targetState == BackStack.TransitionState.STASHED_IN_BACK_STACK }

    override fun onBackPressed() {
        routingSource.pop()
    }
}
