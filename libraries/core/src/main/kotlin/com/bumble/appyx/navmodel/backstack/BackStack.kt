package com.bumble.appyx.navmodel.backstack

import android.os.Parcelable
import com.bumble.appyx.core.navigation.BaseNavModel
import com.bumble.appyx.core.navigation.Operation.Noop
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.core.navigation.backpresshandlerstrategies.BackPressHandlerStrategy
import com.bumble.appyx.core.navigation.onscreen.OnScreenStateResolver
import com.bumble.appyx.core.navigation.operationstrategies.ExecuteImmediately
import com.bumble.appyx.core.navigation.operationstrategies.OperationStrategy
import com.bumble.appyx.navmodel.backstack.BackStack.State
import com.bumble.appyx.navmodel.backstack.BackStack.State.DESTROYED
import com.bumble.appyx.navmodel.backstack.backpresshandler.PopBackPressHandler
import com.bumble.appyx.core.state.SavedStateMap
import com.bumble.appyx.navmodel.backstack.BackStack.State.ACTIVE
import kotlinx.parcelize.Parcelize

class BackStack<NavTarget : Parcelable>(
    initialElement: NavTarget,
    savedStateMap: SavedStateMap?,
    key: String = KEY_NAV_MODEL,
    backPressHandler: BackPressHandlerStrategy<NavTarget, State> = PopBackPressHandler(),
    operationStrategy: OperationStrategy<NavTarget, State> = ExecuteImmediately(),
    screenResolver: OnScreenStateResolver<State> = BackStackOnScreenResolver
) : BaseNavModel<NavTarget, State>(
    backPressHandler = backPressHandler,
    screenResolver = screenResolver,
    operationStrategy = operationStrategy,
    finalState = DESTROYED,
    savedStateMap = savedStateMap,
    key = key,
) {

    @Parcelize
    enum class State : Parcelable {
        CREATED, ACTIVE, STASHED, DESTROYED,
    }

    override val initialElements = listOf(
        BackStackElement(
            key = NavKey(initialElement),
            fromState = ACTIVE,
            targetState = ACTIVE,
            operation = Noop()
        )
    )

}
