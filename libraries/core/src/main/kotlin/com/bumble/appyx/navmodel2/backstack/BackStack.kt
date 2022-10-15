package com.bumble.appyx.navmodel2.backstack

import com.bumble.appyx.core.navigation.BaseNavModel.Companion.KEY_NAV_MODEL
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.core.navigation.onscreen.OnScreenStateResolver
import com.bumble.appyx.core.navigation.operationstrategies.ExecuteImmediately
import com.bumble.appyx.core.navigation.operationstrategies.OperationStrategy
import com.bumble.appyx.core.navigation2.BaseNavModel
import com.bumble.appyx.core.navigation2.Operation.Noop
import com.bumble.appyx.core.navigation2.backpresshandlerstrategies.BackPressHandlerStrategy
import com.bumble.appyx.core.state.SavedStateMap
import com.bumble.appyx.navmodel2.backstack.BackStack.State
import com.bumble.appyx.navmodel2.backstack.BackStack.State.ACTIVE
import com.bumble.appyx.navmodel2.backstack.BackStack.State.DROPPED
import com.bumble.appyx.navmodel2.backstack.BackStack.State.POPPED
import com.bumble.appyx.navmodel2.backstack.BackStack.State.REPLACED
import com.bumble.appyx.navmodel2.backstack.backpresshandler.PopBackPressHandler

@SuppressWarnings("UnusedPrivateMember")
class BackStack<NavTarget : Any>(
    initialElement: NavTarget,
    savedStateMap: SavedStateMap?,
    key: String = KEY_NAV_MODEL,
    backPressHandler: BackPressHandlerStrategy<NavTarget, State> = PopBackPressHandler(),
    operationStrategy: OperationStrategy<NavTarget, State> = ExecuteImmediately(),
    screenResolver: OnScreenStateResolver<State> = BackStackOnScreenResolver
) : BaseNavModel<NavTarget, State>(
//    backPressHandler = backPressHandler,
//    screenResolver = screenResolver,
//    operationStrategy = operationStrategy,
    finalStates = FINAL_STATES,
//    savedStateMap = savedStateMap,
//    key = key,
) {

    enum class State {
        /**
         * Represents an element that's just been created.
         */
        CREATED,

        /**
         * Represents the currently active element.
         * There should be only one such element in the stack.
         */
        ACTIVE,

        /**
         * Represents an element stashed in the back stack (history).
         */
        STASHED,

        /**
         * Represents an element destroyed from an ACTIVE state by Pop.
         */
        POPPED,

        /**
         * Represents an element destroyed from an ACTIVE state by Replace.
         */
        REPLACED,

        /**
         * Represents an element destroyed from a STASHED state.
         */
        DROPPED
    }

    companion object {
        val FINAL_STATES = setOf(POPPED, REPLACED, DROPPED)
    }

    override val initialState = listOf(
        BackStackElement(
            key = NavKey(initialElement),
            fromState = ACTIVE,
            targetState = ACTIVE,
            operation = Noop()
        )
    )
}
