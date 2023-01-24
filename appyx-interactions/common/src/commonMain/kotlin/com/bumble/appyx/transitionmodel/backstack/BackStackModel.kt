package com.bumble.appyx.transitionmodel.backstack

import com.bumble.appyx.interactions.core.BaseTransitionModel
import com.bumble.appyx.interactions.core.NavElement
import com.bumble.appyx.interactions.core.SavedStateMap
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.interactions.core.ui.NavElements
import com.bumble.appyx.transitionmodel.backstack.BackStackModel.State

@SuppressWarnings("UnusedPrivateMember")
class BackStackModel<NavTarget : Any>(
    initialTarget: NavTarget,
    savedStateMap: SavedStateMap?,
    // key: String = KEY_NAV_MODEL,
    // backPressHandler: BackPressHandlerStrategy<NavTarget, State> = PopBackPressHandler(),
    // operationStrategy: OperationStrategy<NavTarget, State> = ExecuteImmediately(),
    // screenResolver: OnScreenStateResolver<State> = BackStackOnScreenResolver
) : BaseTransitionModel<NavTarget, State<NavTarget>>(
//    backPressHandler = backPressHandler,
//    screenResolver = screenResolver,
//    operationStrategy = operationStrategy,
//    savedStateMap = savedStateMap,
//    key = key,
) {
    data class State<NavTarget>(
        /**
         * Elements that have been created, but not yet moved to an active state
         */
        val created: NavElements<NavTarget> = listOf(),

        /**
         * The currently active element.
         * There should be only one such element in the stack.
         */
        val active: NavElement<NavTarget>,

        /**
         * Elements stashed in the back stack (history).
         */
        val stashed: NavElements<NavTarget> = listOf(),

        /**
         * Elements that will be destroyed after reaching this state.
         */
        val destroyed: NavElements<NavTarget> = listOf(),
    )

    override fun State<NavTarget>.availableElements(): Set<NavElement<NavTarget>> =
        (created + active + stashed + destroyed).toSet()

    override fun State<NavTarget>.destroyedElements(): Set<NavElement<NavTarget>> =
        destroyed.toSet()


    override val initialState = State(
        active = initialTarget.asElement(),
    )
}
