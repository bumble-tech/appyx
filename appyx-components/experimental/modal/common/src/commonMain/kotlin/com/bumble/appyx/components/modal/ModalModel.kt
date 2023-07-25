package com.bumble.appyx.components.modal

import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.interactions.core.Elements
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.interactions.core.model.transition.BaseTransitionModel
import com.bumble.appyx.interactions.core.state.SavedStateMap
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

class ModalModel<InteractionTarget : Any>(
    initialElements: List<InteractionTarget>,
    savedStateMap: SavedStateMap?,
) : BaseTransitionModel<InteractionTarget, ModalModel.State<InteractionTarget>>(
    savedStateMap = savedStateMap
) {

    @Parcelize
    data class State<InteractionTarget>(
        /**
         * Elements that have been created, but not yet moved to a modal state
         */
        val created: Elements<InteractionTarget> = listOf(),

        /**
         * Element shown on the screen in a modal form.
         */
        val modal: Element<InteractionTarget>? = null,

        /**
         * Element shown on the screen in a full screen form.
         */
        val fullScreen: Element<InteractionTarget>? = null,

        /**
         * Elements that will be destroyed after reaching this state.
         */
        val destroyed: Elements<InteractionTarget> = listOf(),
    ) : Parcelable

    override val initialState = State(
        created = initialElements.dropLast(1).map { it.asElement() },
        modal = initialElements.last().asElement()
    )

    override fun State<InteractionTarget>.availableElements(): Set<Element<InteractionTarget>> =
        (created + destroyed + listOfNotNull(modal, fullScreen)).toSet()

    override fun State<InteractionTarget>.removeDestroyedElement(element: Element<InteractionTarget>) =
        copy(destroyed = destroyed.filterNot { it == element })

    override fun State<InteractionTarget>.removeDestroyedElements() = copy(destroyed = emptyList())

    override fun State<InteractionTarget>.destroyedElements() = destroyed.toSet()
}
