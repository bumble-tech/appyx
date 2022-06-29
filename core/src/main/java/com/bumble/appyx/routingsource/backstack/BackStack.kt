package com.bumble.appyx.routingsource.backstack

import com.bumble.appyx.core.routing.BaseRoutingSource
import com.bumble.appyx.core.routing.Operation
import com.bumble.appyx.core.routing.Operation.Noop
import com.bumble.appyx.core.routing.RoutingKey
import com.bumble.appyx.core.routing.backpresshandlerstrategies.BackPressHandlerStrategy
import com.bumble.appyx.core.routing.onscreen.OnScreenStateResolver
import com.bumble.appyx.core.routing.operationstrategies.ExecuteImmediately
import com.bumble.appyx.core.routing.operationstrategies.OperationStrategy
import com.bumble.appyx.routingsource.backstack.BackStack.TransitionState
import com.bumble.appyx.routingsource.backstack.BackStack.TransitionState.DESTROYED
import com.bumble.appyx.routingsource.backstack.backpresshandler.PopBackPressHandler
import com.bumble.appyx.core.state.SavedStateMap
import com.bumble.appyx.routingsource.backstack.BackStack.TransitionState.ACTIVE

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
            fromState = ACTIVE,
            targetState = ACTIVE,
            operation = Noop()
        )
    )

}
