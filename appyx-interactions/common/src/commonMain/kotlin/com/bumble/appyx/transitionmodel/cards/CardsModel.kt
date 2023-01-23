package com.bumble.appyx.transitionmodel.cards

import com.bumble.appyx.interactions.core.BaseTransitionModel
import com.bumble.appyx.interactions.core.NavElement
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.interactions.core.ui.NavElements

class CardsModel<NavTarget : Any>(
    initialItems: List<NavTarget> = listOf(),
) : BaseTransitionModel<NavTarget, CardsModel.State<NavTarget>>() {

    data class State<NavTarget>(
        val queued: NavElements<NavTarget> = listOf(),
        val liked: NavElements<NavTarget> = listOf(),
        val passed: NavElements<NavTarget> = listOf()
    )

    override val initialState: State<NavTarget> =
        State(queued = initialItems.map { it.asElement() })

    override fun State<NavTarget>.destroyedElements(): Set<NavElement<NavTarget>> = setOf()
}

