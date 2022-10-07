package com.bumble.appyx.navmodel2.spotlight

import com.bumble.appyx.core.navigation2.BaseNavModel
import com.bumble.appyx.core.navigation.BaseNavModel.Companion.KEY_NAV_MODEL
import com.bumble.appyx.core.navigation2.backpresshandlerstrategies.BackPressHandlerStrategy
import com.bumble.appyx.core.navigation.onscreen.OnScreenStateResolver
import com.bumble.appyx.core.navigation.operationstrategies.ExecuteImmediately
import com.bumble.appyx.core.navigation.operationstrategies.OperationStrategy
import com.bumble.appyx.core.navigation2.NavElements
import com.bumble.appyx.core.state.SavedStateMap
import com.bumble.appyx.navmodel2.spotlight.Spotlight.State
import com.bumble.appyx.navmodel2.spotlight.operation.toSpotlightElements

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
) : BaseNavModel<NavTarget, State>(
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
