package com.bumble.appyx.interactions.permanent

import com.bumble.appyx.interactions.model.Element
import com.bumble.appyx.interactions.model.Elements
import com.bumble.appyx.interactions.model.asElement
import com.bumble.appyx.interactions.model.transition.BaseTransitionModel
import com.bumble.appyx.interactions.permanent.PermanentModel.State
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import com.bumble.appyx.utils.multiplatform.SavedStateMap

class PermanentModel<InteractionTarget : Any>(
    savedStateMap: SavedStateMap?,
    initialTargets: List<InteractionTarget> = emptyList(),
    key: String = KEY_PERMANENT_TRANSITION_MODEL
) : BaseTransitionModel<InteractionTarget, State<InteractionTarget>>(
    key = key,
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

    override fun State<InteractionTarget>.removeDestroyedElement(
        element: Element<InteractionTarget>
    ): State<InteractionTarget> =
        this

    override fun State<InteractionTarget>.removeDestroyedElements(): State<InteractionTarget> =
        this

    override val initialState = State(
        elements = initialTargets.map { it.asElement() }
    )

    private companion object {
        private const val KEY_PERMANENT_TRANSITION_MODEL = "PermanentTransitionModel"
    }
}
