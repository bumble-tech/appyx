package com.bumble.appyx.v2.core.routing.source.backstack

import com.bumble.appyx.v2.core.routing.BaseRoutingSource
import com.bumble.appyx.v2.core.routing.Operation
import com.bumble.appyx.v2.core.routing.RoutingKey
import com.bumble.appyx.v2.core.routing.backpresshandlerstrategies.BackPressHandlerStrategy
import com.bumble.appyx.v2.core.routing.onscreen.OnScreenStateResolver
import com.bumble.appyx.v2.core.routing.operationstrategies.ExecuteImmediately
import com.bumble.appyx.v2.core.routing.operationstrategies.OperationStrategy
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack.TransitionState
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack.TransitionState.DESTROYED
import com.bumble.appyx.v2.core.routing.source.backstack.backpresshandler.PopBackPressHandler
import com.bumble.appyx.v2.core.state.SavedStateMap

class BackStack<Routing : Any>(
    initialElement: Routing,
    savedStateMap: SavedStateMap?,
    key: String = KEY_ROUTING_SOURCE,
    backPressHandler: BackPressHandlerStrategy<Routing, TransitionState> = PopBackPressHandler(),
    operationStrategy: OperationStrategy<Routing, TransitionState> = ExecuteImmediately(),
    screenResolver: OnScreenStateResolver<TransitionState> = BackStackOnScreenResolver
) : BaseRoutingSource<Routing, TransitionState>(
    backPressHandler = backPressHandler,
    screenResolver = screenResolver,
    operationStrategy = operationStrategy,
    finalState = DESTROYED,
    savedStateMap = savedStateMap,
    key = key,
) {

    enum class TransitionState {
        CREATED, ACTIVE, STASHED_IN_BACK_STACK, DESTROYED,
    }

    override val initialElements = listOf(
        BackStackElement(
            key = RoutingKey(initialElement),
            fromState = TransitionState.ACTIVE,
            targetState = TransitionState.ACTIVE,
            operation = Operation.Noop()
        )
    )

}
