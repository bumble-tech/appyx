package com.bumble.appyx.navmodel.modal

import com.bumble.appyx.core.navigation.BaseNavModel
import com.bumble.appyx.core.navigation.Operation.Noop
import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.core.navigation.backpresshandlerstrategies.BackPressHandlerStrategy
import com.bumble.appyx.core.navigation.onscreen.OnScreenStateResolver
import com.bumble.appyx.core.navigation.operationstrategies.ExecuteImmediately
import com.bumble.appyx.core.navigation.operationstrategies.OperationStrategy
import com.bumble.appyx.core.state.SavedStateMap
import com.bumble.appyx.navmodel.modal.Modal.TransitionState
import com.bumble.appyx.navmodel.modal.Modal.TransitionState.CREATED
import com.bumble.appyx.navmodel.modal.Modal.TransitionState.DESTROYED
import com.bumble.appyx.navmodel.modal.backpresshandler.RevertBackPressHandler

class Modal<NavTarget : Any>(
    initialElement: NavTarget,
    savedStateMap: SavedStateMap?,
    key: String = KEY_NAV_MODEL,
    backPressHandler: BackPressHandlerStrategy<NavTarget, TransitionState> = RevertBackPressHandler(),
    operationStrategy: OperationStrategy<NavTarget, TransitionState> = ExecuteImmediately(),
    screenResolver: OnScreenStateResolver<TransitionState> = ModalOnScreenResolver
) : BaseNavModel<NavTarget, TransitionState>(
    savedStateMap = savedStateMap,
    screenResolver = screenResolver,
    operationStrategy = operationStrategy,
    backPressHandler = backPressHandler,
    key = key,
    finalState = DESTROYED
) {

    enum class TransitionState {
        CREATED, MODAL, FULL_SCREEN, DESTROYED
    }

    override val initialElements: NavElements<NavTarget, TransitionState> = listOf(
        ModalElement(
            key = NavKey(initialElement),
            fromState = CREATED,
            targetState = CREATED,
            operation = Noop()
        )
    )
}
