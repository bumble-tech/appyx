package com.bumble.appyx.navmodel.spotlight

import android.os.Parcelable
import com.bumble.appyx.core.navigation.BaseNavModel
import com.bumble.appyx.core.navigation.backpresshandlerstrategies.BackPressHandlerStrategy
import com.bumble.appyx.core.navigation.onscreen.OnScreenStateResolver
import com.bumble.appyx.core.navigation.operationstrategies.ExecuteImmediately
import com.bumble.appyx.core.navigation.operationstrategies.OperationStrategy
import com.bumble.appyx.core.state.SavedStateMap
import com.bumble.appyx.navmodel.spotlight.Spotlight.State
import com.bumble.appyx.navmodel.spotlight.backpresshandler.GoToDefault
import com.bumble.appyx.navmodel.spotlight.operation.toSpotlightElements
import kotlinx.parcelize.Parcelize

class Spotlight<NavTarget : Parcelable>(
    items: List<NavTarget>,
    initialActiveIndex: Int = 0,
    savedStateMap: SavedStateMap?,
    key: String = KEY_NAV_MODEL,
    backPressHandler: BackPressHandlerStrategy<NavTarget, State> = GoToDefault(
        initialActiveIndex
    ),
    operationStrategy: OperationStrategy<NavTarget, State> = ExecuteImmediately(),
    screenResolver: OnScreenStateResolver<State> = SpotlightOnScreenResolver
) : BaseNavModel<NavTarget, State>(
    backPressHandler = backPressHandler,
    operationStrategy = operationStrategy,
    screenResolver = screenResolver,
    finalState = null,
    savedStateMap = savedStateMap,
    key = key,
) {

    @Parcelize
    enum class State : Parcelable {
        INACTIVE_BEFORE, ACTIVE, INACTIVE_AFTER;
    }

    override val initialElements = items.toSpotlightElements(initialActiveIndex)

}
