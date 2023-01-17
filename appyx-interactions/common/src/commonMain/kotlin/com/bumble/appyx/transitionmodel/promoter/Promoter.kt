package com.bumble.appyx.transitionmodel.promoter

import com.bumble.appyx.transitionmodel.promoter.Promoter.State
import com.bumble.appyx.transitionmodel.promoter.Promoter.State.CREATED
import com.bumble.appyx.transitionmodel.promoter.Promoter.State.STAGE1
import com.bumble.appyx.interactions.core.BaseTransitionModel
import com.bumble.appyx.interactions.core.NavElement
import com.bumble.appyx.interactions.core.NavElements
import com.bumble.appyx.interactions.core.NavKey
import com.bumble.appyx.interactions.core.Operation

class Promoter<NavTarget : Any>(
    initialItems: List<NavTarget> = listOf(),
) : BaseTransitionModel<NavTarget, State>(
//    screenResolver = PromoterOnScreenResolver,
//    finalState = DESTROYED,
//    savedStateMap = null
) {

    enum class State {
        CREATED, STAGE1, STAGE2, STAGE3, STAGE4, SELECTED, DESTROYED;

        fun next(): State =
            when (this) {
                CREATED -> STAGE1
                STAGE1 -> STAGE2
                STAGE2 -> STAGE3
                STAGE3 -> STAGE4
                STAGE4 -> SELECTED
                SELECTED -> DESTROYED
                DESTROYED -> DESTROYED
            }
    }

    override val initialState: NavElements<NavTarget, State> = initialItems.map {
        NavElement(
            key = NavKey(it),
            fromState = CREATED,
            targetState = STAGE1,
            operation = Operation.Noop()
        )
    }
}
