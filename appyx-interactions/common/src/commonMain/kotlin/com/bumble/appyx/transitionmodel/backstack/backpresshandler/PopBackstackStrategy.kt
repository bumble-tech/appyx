package com.bumble.appyx.transitionmodel.backstack.backpresshandler

import com.bumble.appyx.interactions.core.model.backpresshandlerstrategies.BaseBackPressHandlerStrategy
import com.bumble.appyx.mapState
import com.bumble.appyx.transitionmodel.backstack.BackStackModel
import com.bumble.appyx.transitionmodel.backstack.operation.Pop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PopBackstackStrategy<InteractionTarget : Any>(val scope: CoroutineScope) :
    BaseBackPressHandlerStrategy<InteractionTarget, BackStackModel.State<InteractionTarget>>() {

    override val canHandleBackPress: StateFlow<Boolean> by lazy {
        MutableStateFlow(false)
        transitionModel.output.mapState(scope) { output ->
            output.currentTargetState.stashed.isNotEmpty()
        }
    }

    override fun handleBackPress(): Boolean {
        val pop = Pop<InteractionTarget>()
        //todo find a better way to check if operation is applicable
        return if (pop.isApplicable(transitionModel.output.value.currentTargetState)) {
            interactionModel.operation(Pop())
            true
        } else false
    }
}
