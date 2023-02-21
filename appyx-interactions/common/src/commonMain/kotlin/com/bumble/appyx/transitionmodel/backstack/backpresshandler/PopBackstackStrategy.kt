package com.bumble.appyx.transitionmodel.backstack.backpresshandler

import com.bumble.appyx.interactions.core.backpresshandlerstrategies.BaseBackPressHandlerStrategy
import com.bumble.appyx.transitionmodel.backstack.BackStackModel
import com.bumble.appyx.transitionmodel.backstack.operation.Pop
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PopBackstackStrategy<NavTarget : Any> :
    BaseBackPressHandlerStrategy<NavTarget, BackStackModel.State<NavTarget>>() {
    override val canHandleBackPress: Flow<Boolean> by lazy {
        transitionModel.output.map { it.currentTargetState }.map { it.stashed.isNotEmpty() }
    }

    override fun handleUpNavigation(): Boolean {
        val pop = Pop<NavTarget>()
        //todo find a better way to check if operation is applicable
        return if (pop.isApplicable(transitionModel.output.value.currentTargetState)) {
            interactionModel.operation(Pop())
            true
        } else false
    }
}