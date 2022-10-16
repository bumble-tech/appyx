package com.bumble.appyx.navmodel.promoter.navmodel2

import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.navmodel.promoter.navmodel2.Promoter.State
import com.bumble.appyx.navmodel.promoter.navmodel2.Promoter.State.CREATED
import com.bumble.appyx.navmodel.promoter.navmodel2.Promoter.State.STAGE1
import com.bumble.appyx.core.navigation2.BaseNavModel
import com.bumble.appyx.core.navigation2.NavElement
import com.bumble.appyx.core.navigation2.NavElements
import com.bumble.appyx.core.navigation2.Operation.Noop

class Promoter<NavTarget : Any>(
    initialItems: List<NavTarget> = listOf(),
) : BaseNavModel<NavTarget, State>(
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
            operation = Noop()
        )
    }
}
