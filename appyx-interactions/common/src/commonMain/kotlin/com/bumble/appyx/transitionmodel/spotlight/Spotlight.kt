package com.bumble.appyx.transitionmodel.spotlight

import com.bumble.appyx.interactions.core.BaseTransitionModel
import com.bumble.appyx.interactions.core.NavElements
import com.bumble.appyx.transitionmodel.spotlight.Spotlight.State
import com.bumble.appyx.transitionmodel.spotlight.operation.toSpotlightElements

class Spotlight<NavTarget : Any>(
    private val items: List<NavTarget>,
    private val initialActiveIndex: Int = 0,
//    savedStateMap: SavedStateMap?,
//    key: String = KEY_NAV_MODEL,
//    backPressHandler: BackPressHandlerStrategy<NavTarget, State> = GoToDefault(
//        initialActiveIndex
//    ),
//    operationStrategy: OperationStrategy<NavTarget, State> = ExecuteImmediately(),
//    screenResolver: OnScreenStateResolver<State> = SpotlightOnScreenResolver
) : BaseTransitionModel<NavTarget, State>(
//    backPressHandler = backPressHandler,
//    operationStrategy = operationStrategy,
//    screenResolver = screenResolver,
//    finalState = null,
//    savedStateMap = savedStateMap,
//    key = key
) {

    enum class State {
        INACTIVE_BEFORE, ACTIVE, INACTIVE_AFTER;
    }

    override val initialState: NavElements<NavTarget, State> =
        items.toSpotlightElements(initialActiveIndex)
}
