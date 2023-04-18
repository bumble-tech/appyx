package com.bumble.appyx.interactions.permanent

import com.bumble.appyx.interactions.Parcelable
import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.interactions.core.Elements
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.interactions.core.model.transition.BaseTransitionModel
import com.bumble.appyx.interactions.core.state.SavedStateMap
import com.bumble.appyx.interactions.permanent.PermanentModel.State

class PermanentModel<InteractionTarget : Any>(
    initialTargets: List<InteractionTarget>,
    savedStateMap: SavedStateMap?,
) : BaseTransitionModel<InteractionTarget, State<InteractionTarget>>(
    savedStateMap = savedStateMap,
) {

    @Parcelize
    data class State<InteractionTarget>(
        val elements: Elements<InteractionTarget>
    ) : Parcelable

    override fun State<InteractionTarget>.availableElements(): Set<Element<InteractionTarget>> =
        elements.toSet()

    override fun State<InteractionTarget>.destroyedElements(): Set<Element<InteractionTarget>> =
        emptySet()

    override fun State<InteractionTarget>.removeDestroyedElement(element: Element<InteractionTarget>): State<InteractionTarget> =
        this

    override fun State<InteractionTarget>.removeDestroyedElements(): State<InteractionTarget> =
        this

    override val initialState = State(
        elements = initialTargets.map { it.asElement() }
    )
}
