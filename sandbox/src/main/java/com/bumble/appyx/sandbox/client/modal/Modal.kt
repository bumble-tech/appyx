package com.bumble.appyx.sandbox.client.modal

import com.bumble.appyx.core.routing.BaseRoutingSource
import com.bumble.appyx.core.routing.Operation.Noop
import com.bumble.appyx.core.routing.RoutingElements
import com.bumble.appyx.core.routing.RoutingKey
import com.bumble.appyx.core.routing.backpresshandlerstrategies.BackPressHandlerStrategy
import com.bumble.appyx.core.routing.onscreen.OnScreenStateResolver
import com.bumble.appyx.core.routing.operationstrategies.ExecuteImmediately
import com.bumble.appyx.core.routing.operationstrategies.OperationStrategy
import com.bumble.appyx.core.state.SavedStateMap
import com.bumble.appyx.sandbox.client.modal.Modal.TransitionState.CREATED
import com.bumble.appyx.sandbox.client.modal.Modal.TransitionState.DESTROYED
import com.bumble.appyx.sandbox.client.modal.backpresshandler.RevertBackPressHandler

class Modal<Routing : Any>(
    initialElement: Routing,
    savedStateMap: SavedStateMap?,
    key: String = KEY_ROUTING_SOURCE,
    backPressHandler: BackPressHandlerStrategy<Routing, TransitionState> = RevertBackPressHandler(),
    operationStrategy: OperationStrategy<Routing, TransitionState> = ExecuteImmediately(),
    screenResolver: OnScreenStateResolver<TransitionState> = ModalOnScreenResolver
) : BaseRoutingSource<Routing, Modal.TransitionState>(
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

    override val initialElements: RoutingElements<Routing, TransitionState> = listOf(
        ModalElement(
            key = RoutingKey(initialElement),
            fromState = CREATED,
            targetState = CREATED,
            operation = Noop()
        )
    )
}
