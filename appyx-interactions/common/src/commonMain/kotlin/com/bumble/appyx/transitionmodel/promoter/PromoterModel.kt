package com.bumble.appyx.transitionmodel.promoter

import com.bumble.appyx.interactions.core.model.transition.BaseTransitionModel
import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.transitionmodel.promoter.PromoterModel.State.ElementState.DESTROYED

class PromoterModel<InteractionTarget : Any>(
) : BaseTransitionModel<InteractionTarget, PromoterModel.State<InteractionTarget>>(
//    screenResolver = PromoterOnScreenResolver,
//    finalState = DESTROYED,
//    savedStateMap = null
) {
    data class State<InteractionTarget>(
        val elements: List<Pair<Element<InteractionTarget>, ElementState>>
    ) {
        enum class ElementState {
            CREATED, STAGE1, STAGE2, STAGE3, STAGE4, STAGE5, DESTROYED;

            fun next(): ElementState =
                when (this) {
                    CREATED -> STAGE1
                    STAGE1 -> STAGE2
                    STAGE2 -> STAGE3
                    STAGE3 -> STAGE4
                    STAGE4 -> STAGE5
                    STAGE5 -> DESTROYED
                    DESTROYED -> DESTROYED
                }
        }
    }

    override fun State<InteractionTarget>.availableElements(): Set<Element<InteractionTarget>> =
        elements
            .filter { it.second != DESTROYED }
            .map { it.first }
            .toSet()


    override fun State<InteractionTarget>.destroyedElements(): Set<Element<InteractionTarget>> =
        elements
            .filter { it.second == DESTROYED }
            .map { it.first }
            .toSet()

    override val initialState: State<InteractionTarget> =
        State(elements = listOf())

    override fun State<InteractionTarget>.removeDestroyedElement(element: Element<InteractionTarget>): State<InteractionTarget> =
        copy(elements.filterNot { it.first == element && it.second == DESTROYED })

    override fun State<InteractionTarget>.removeDestroyedElements(): State<InteractionTarget> =
        copy(
            this.elements.filter { pair -> pair.second != DESTROYED }
        )
}
