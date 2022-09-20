package com.bumble.appyx.navmodel.spotlightadvanced

import com.bumble.appyx.core.navigation.BaseNavModel
import com.bumble.appyx.core.navigation.backpresshandlerstrategies.BackPressHandlerStrategy
import com.bumble.appyx.core.navigation.onscreen.OnScreenStateResolver
import com.bumble.appyx.core.navigation.operationstrategies.IgnoreIfThereAreUnfinishedTransitions
import com.bumble.appyx.core.navigation.operationstrategies.OperationStrategy
import com.bumble.appyx.core.state.SavedStateMap
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.State
import com.bumble.appyx.navmodel.spotlightadvanced.backpresshandler.GoToDefault
import com.bumble.appyx.navmodel.spotlightadvanced.operation.toSpotlightAdvancedElements

class SpotlightAdvanced<NavTarget : Any>(
    items: List<NavTarget>,
    initialActiveIndex: Int = 0,
    savedStateMap: SavedStateMap?,
    key: String = KEY_NAV_MODEL,
    backPressHandler: BackPressHandlerStrategy<NavTarget, State> =
        GoToDefault(initialActiveIndex),
    operationStrategy: OperationStrategy<NavTarget, State> = IgnoreIfThereAreUnfinishedTransitions(),
    screenResolver: OnScreenStateResolver<State> = SpotlightAdvancedOnScreenResolver
) : BaseNavModel<NavTarget, State>(
    backPressHandler = backPressHandler,
    operationStrategy = operationStrategy,
    screenResolver = screenResolver,
    finalState = null,
    savedStateMap = savedStateMap,
    key = key,
) {

    sealed class State {
        object InactiveBefore : State()
        object Active : State()
        object InactiveAfter : State()
        data class Carousel(val offset: Int, val max: Int) : State()
    }

    override val initialElements = items.toSpotlightAdvancedElements(initialActiveIndex)

}
