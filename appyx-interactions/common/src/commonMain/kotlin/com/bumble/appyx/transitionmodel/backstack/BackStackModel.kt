package com.bumble.appyx.transitionmodel.backstack

import com.bumble.appyx.interactions.core.model.transition.BaseTransitionModel
import com.bumble.appyx.interactions.core.NavElement
import com.bumble.appyx.interactions.core.SavedStateMap
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.interactions.core.ui.NavElements
import com.bumble.appyx.transitionmodel.backstack.BackStackModel.State

@SuppressWarnings("UnusedPrivateMember")
class BackStackModel<InteractionTarget : Any>(
    initialTargets: List<InteractionTarget>,
    savedStateMap: SavedStateMap?,
    // key: String = KEY_NAV_MODEL,
    // backPressHandler: BackPressHandlerStrategy<InteractionTarget, State> = PopBackPressHandler(),
    // operationStrategy: OperationStrategy<InteractionTarget, State> = ExecuteImmediately(),
    // screenResolver: OnScreenStateResolver<State> = BackStackOnScreenResolver
) : BaseTransitionModel<InteractionTarget, State<InteractionTarget>>(
//    backPressHandler = backPressHandler,
//    screenResolver = screenResolver,
//    operationStrategy = operationStrategy,
//    savedStateMap = savedStateMap,
//    key = key,
) {
    data class State<InteractionTarget>(
        /**
         * Elements that have been created, but not yet moved to an active state
         */
        val created: NavElements<InteractionTarget> = listOf(),

        /**
         * The currently active element.
         * There should be only one such element in the stack.
         */
        val active: NavElement<InteractionTarget>,

        /**
         * Elements stashed in the back stack (history).
         */
        val stashed: NavElements<InteractionTarget> = listOf(),

        /**
         * Elements that will be destroyed after reaching this state.
         */
        val destroyed: NavElements<InteractionTarget> = listOf(),
    )

    override fun State<InteractionTarget>.availableElements(): Set<NavElement<InteractionTarget>> =
        (created + active + stashed + destroyed).toSet()

    override fun State<InteractionTarget>.destroyedElements(): Set<NavElement<InteractionTarget>> =
        destroyed.toSet()

    override fun State<InteractionTarget>.removeDestroyedElement(navElement: NavElement<InteractionTarget>): State<InteractionTarget> =
        copy(destroyed = destroyed.filterNot { it == navElement })

    override fun State<InteractionTarget>.removeDestroyedElements(): State<InteractionTarget> =
        copy(destroyed = emptyList())

    override val initialState = State(
        active = initialTargets.last().asElement(),
        stashed = initialTargets.take(initialTargets.size - 1).map { it.asElement() }
    )
}
