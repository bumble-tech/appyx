package com.bumble.appyx.navmodel.modal.backpresshandler

import com.bumble.appyx.core.navigation.backpresshandlerstrategies.BaseBackPressHandlerStrategy
import com.bumble.appyx.navmodel.modal.Modal.TransitionState
import com.bumble.appyx.navmodel.modal.Modal.TransitionState.FULL_SCREEN
import com.bumble.appyx.navmodel.modal.ModalElements
import com.bumble.appyx.navmodel.modal.operation.Revert
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RevertBackPressHandler<Routing : Any> :
    BaseBackPressHandlerStrategy<Routing, TransitionState>() {

    override val canHandleBackPressFlow: Flow<Boolean> by lazy {
        navModel.elements.map(::areThereFullScreenElements)
    }

    private fun areThereFullScreenElements(elements: ModalElements<Routing>) =
        elements.any { it.targetState == FULL_SCREEN }

    override fun onBackPressed() {
        navModel.accept(Revert())
    }
}
