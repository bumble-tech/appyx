package com.bumble.appyx.components.backstack

import com.bumble.appyx.components.backstack.BackStackModel.State
import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.interactions.core.Elements
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.interactions.core.model.transition.BaseTransitionModel
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import com.bumble.appyx.utils.multiplatform.SavedStateMap

class BackStackModel<NavTarget : Any>(
    initialTargets: List<NavTarget>,
    savedStateMap: SavedStateMap?,
) : BaseTransitionModel<NavTarget, State<NavTarget>>(
    savedStateMap = savedStateMap,
) {
    @Parcelize
    data class State<NavTarget>(
        /**
         * Elements that have been created, but not yet moved to an active state
         */
        val created: Elements<NavTarget> = listOf(),

        /**
         * The currently active element.
         * There should be only one such element in the stack.
         */
        val active: Element<NavTarget>,

        /**
         * Elements stashed in the back stack (history).
         */
        val stashed: Elements<NavTarget> = listOf(),

        /**
         * Elements that will be destroyed after reaching this state.
         */
        val destroyed: Elements<NavTarget> = listOf(),
    ) : Parcelable

    constructor(
        initialTarget: NavTarget,
        savedStateMap: SavedStateMap?,
    ) : this(
        initialTargets = listOf(initialTarget),
        savedStateMap = savedStateMap
    )

    override fun State<NavTarget>.availableElements(): Set<Element<NavTarget>> =
        (created + active + stashed + destroyed).toSet()

    override fun State<NavTarget>.destroyedElements(): Set<Element<NavTarget>> =
        destroyed.toSet()

    override fun State<NavTarget>.removeDestroyedElement(
        element: Element<NavTarget>
    ): State<NavTarget> =
        copy(destroyed = destroyed.filterNot { it == element })

    override fun State<NavTarget>.removeDestroyedElements(): State<NavTarget> =
        copy(destroyed = emptyList())

    override val initialState = State(
        active = initialTargets.last().asElement(),
        stashed = initialTargets.take(initialTargets.size - 1).map { it.asElement() }
    )
}
