package com.bumble.appyx.navmodel.promoter.navmodel

import com.bumble.appyx.core.navigation.BaseNavModel
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.core.navigation.Operation
import com.bumble.appyx.navmodel.promoter.navmodel.Promoter.State
import com.bumble.appyx.navmodel.promoter.navmodel.Promoter.State.DESTROYED

class Promoter<T : Any>(
    initialItems: List<T> = listOf(),
) : BaseNavModel<T, State>(
    screenResolver = PromoterOnScreenResolver,
    finalState = DESTROYED,
    initialElements = initialItems.map {
        PromoterElement(
            key = NavKey(it),
            fromState = State.CREATED,
            targetState = State.STAGE1,
            operation = Operation.Noop()
        )
    },
    savedStateMap = null,
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

}
