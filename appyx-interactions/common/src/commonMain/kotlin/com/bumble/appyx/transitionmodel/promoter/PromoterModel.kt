package com.bumble.appyx.transitionmodel.promoter

import com.bumble.appyx.interactions.core.BaseTransitionModel
import com.bumble.appyx.interactions.core.NavElement
import com.bumble.appyx.transitionmodel.promoter.PromoterModel.State.ElementState.DESTROYED

class PromoterModel<NavTarget : Any>(
) : BaseTransitionModel<NavTarget, PromoterModel.State<NavTarget>>(
//    screenResolver = PromoterOnScreenResolver,
//    finalState = DESTROYED,
//    savedStateMap = null
) {
    data class State<NavTarget>(
        val elements: List<Pair<NavElement<NavTarget>, ElementState>>
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

    override fun State<NavTarget>.availableElements(): Set<NavElement<NavTarget>> =
        elements
            .filter { it.second != DESTROYED }
            .map { it.first }
            .toSet()


    override fun State<NavTarget>.destroyedElements(): Set<NavElement<NavTarget>> =
        elements
            .filter { it.second == DESTROYED }
            .map { it.first }
            .toSet()

    override val initialState: State<NavTarget> =
        State(elements = listOf())

    override fun State<NavTarget>.removeDestroyedElement(navElement: NavElement<NavTarget>): State<NavTarget> =
        copy(elements.filterNot { it.first == navElement && it.second == DESTROYED })

    override fun State<NavTarget>.removeDestroyedElements(): State<NavTarget> =
        copy(
            this.elements.filter { pair -> pair.second != DESTROYED }
        )
}
