package com.bumble.appyx.transitionmodel.backstack

import com.bumble.appyx.interactions.Parcelable
import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.interactions.core.Elements
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.interactions.core.model.transition.BaseTransitionModel
import com.bumble.appyx.interactions.core.state.SavedStateMap
import com.bumble.appyx.transitionmodel.backstack.BackStackModel.State

class BackStackModel<InteractionTarget : Any>(
    initialTargets: List<InteractionTarget>,
    savedStateMap: SavedStateMap?,
) : BaseTransitionModel<InteractionTarget, State<InteractionTarget>>(
    savedStateMap = savedStateMap,
) {
    @Parcelize
    data class State<InteractionTarget>(
        /**
         * Elements that have been created, but not yet moved to an active state
         */
        val created: Elements<InteractionTarget> = listOf(),

        /**
         * The currently active element.
         * There should be only one such element in the stack.
         */
        val active: Element<InteractionTarget>,

        /**
         * Elements stashed in the back stack (history).
         */
        val stashed: Elements<InteractionTarget> = listOf(),

        /**
         * Elements that will be destroyed after reaching this state.
         */
        val destroyed: Elements<InteractionTarget> = listOf(),
    ) : Parcelable

    override fun State<InteractionTarget>.availableElements(): Set<Element<InteractionTarget>> =
        (created + active + stashed + destroyed).toSet()

    override fun State<InteractionTarget>.destroyedElements(): Set<Element<InteractionTarget>> =
        destroyed.toSet()

    override fun State<InteractionTarget>.removeDestroyedElement(element: Element<InteractionTarget>): State<InteractionTarget> =
        copy(destroyed = destroyed.filterNot { it == element })

    override fun State<InteractionTarget>.removeDestroyedElements(): State<InteractionTarget> =
        copy(destroyed = emptyList())

    override val initialState = State(
        active = initialTargets.last().asElement(),
        stashed = initialTargets.take(initialTargets.size - 1).map { it.asElement() }
    )
}
