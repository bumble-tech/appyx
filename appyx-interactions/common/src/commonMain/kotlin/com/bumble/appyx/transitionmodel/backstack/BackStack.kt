package com.bumble.appyx.transitionmodel.backstack

import com.bumble.appyx.interactions.core.NavKey
import com.bumble.appyx.interactions.core.BaseNavModel
import com.bumble.appyx.interactions.core.Operation
import com.bumble.appyx.interactions.core.SavedStateMap
import com.bumble.appyx.transitionmodel.backstack.BackStack.State
import com.bumble.appyx.transitionmodel.backstack.BackStack.State.ACTIVE
import com.bumble.appyx.transitionmodel.backstack.BackStack.State.DROPPED
import com.bumble.appyx.transitionmodel.backstack.BackStack.State.POPPED
import com.bumble.appyx.transitionmodel.backstack.BackStack.State.REPLACED

@SuppressWarnings("UnusedPrivateMember")
class BackStack<NavTarget : Any>(
    initialElement: NavTarget,
    savedStateMap: SavedStateMap?,
    // key: String = KEY_NAV_MODEL,
    // backPressHandler: BackPressHandlerStrategy<NavTarget, State> = PopBackPressHandler(),
    // operationStrategy: OperationStrategy<NavTarget, State> = ExecuteImmediately(),
    // screenResolver: OnScreenStateResolver<State> = BackStackOnScreenResolver
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
            operation = Operation.Noop()
        )
    )
}