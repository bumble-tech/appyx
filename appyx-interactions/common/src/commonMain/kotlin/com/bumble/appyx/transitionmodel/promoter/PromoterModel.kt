package com.bumble.appyx.transitionmodel.promoter

import com.bumble.appyx.interactions.core.BaseTransitionModel
import com.bumble.appyx.interactions.core.NavElement
import com.bumble.appyx.interactions.core.NavTransition
import com.bumble.appyx.interactions.core.asElements
import com.bumble.appyx.interactions.core.ui.NavElements

class PromoterModel<NavTarget : Any>(
    initialItems: List<NavTarget> = listOf(),
    val activeElementCount: Int = 6,
) : BaseTransitionModel<NavTarget, PromoterModel.State<NavTarget>>(
//    screenResolver = PromoterOnScreenResolver,
//    finalState = DESTROYED,
//    savedStateMap = null
) {
    data class State<NavTarget>(
        val activeStartAtIndex: Int,
        val activeElementCount: Int,
        val elements: NavElements<NavTarget>,
    )

    override fun State<NavTarget>.destroyedElements(): Set<NavElement<NavTarget>> =
        elements.drop(activeStartAtIndex).drop(activeElementCount).toSet()

    override val initialState: State<NavTarget> =
        State(
            activeStartAtIndex = initialItems.lastIndex,
            activeElementCount = activeElementCount,
            elements = initialItems.asElements(),
        )

    override val initialTransition: NavTransition<State<NavTarget>> by lazy {
        NavTransition(
            fromState = initialState,
            targetState = initialState.copy(
                activeStartAtIndex = 0
            ),
        )
    }
}
