package com.bumble.appyx.navmodel.spotlightadvanced

import com.bumble.appyx.core.navigation.BaseNavModel
import com.bumble.appyx.core.navigation.backpresshandlerstrategies.BackPressHandlerStrategy
import com.bumble.appyx.core.navigation.onscreen.OnScreenStateResolver
import com.bumble.appyx.core.navigation.operationstrategies.IgnoreIfThereAreUnfinishedTransitions
import com.bumble.appyx.core.navigation.operationstrategies.OperationStrategy
import com.bumble.appyx.core.state.SavedStateMap
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.TransitionState
import com.bumble.appyx.navmodel.spotlightadvanced.backpresshandler.GoToDefault
import com.bumble.appyx.navmodel.spotlightadvanced.operation.toSpotlightAdvancedElements

class SpotlightAdvanced<Routing : Any>(
    items: List<Routing>,
    initialActiveIndex: Int = 0,
    savedStateMap: SavedStateMap?,
    key: String = KEY_NAV_MODEL,
    backPressHandler: BackPressHandlerStrategy<Routing, TransitionState> =
        GoToDefault(initialActiveIndex),
    operationStrategy: OperationStrategy<Routing, TransitionState> = IgnoreIfThereAreUnfinishedTransitions(),
    screenResolver: OnScreenStateResolver<TransitionState> = SpotlightAdvancedOnScreenResolver
) : BaseNavModel<Routing, TransitionState>(
    backPressHandler = backPressHandler,
    operationStrategy = operationStrategy,
    screenResolver = screenResolver,
    finalState = null,
    savedStateMap = savedStateMap,
    key = key,
) {

    sealed class TransitionState {
        object InactiveBefore : TransitionState()
        object Active : TransitionState()
        object InactiveAfter : TransitionState()
        data class Carousel(val offset: Int, val max: Int) : TransitionState()
    }

    override val initialElements = items.toSpotlightAdvancedElements(initialActiveIndex)

}
