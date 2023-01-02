package com.bumble.appyx.navmodel.promoter.navmodel

import android.os.Parcelable
import com.bumble.appyx.navmodel.promoter.navmodel.Promoter.State
import com.bumble.appyx.navmodel.promoter.navmodel.Promoter.State.CREATED
import com.bumble.appyx.navmodel.promoter.navmodel.Promoter.State.DESTROYED
import com.bumble.appyx.navmodel.promoter.navmodel.Promoter.State.STAGE1
import com.bumble.appyx.core.navigation.BaseNavModel
import com.bumble.appyx.core.navigation.Operation.Noop
import com.bumble.appyx.core.navigation.NavKey
import kotlinx.parcelize.Parcelize

class Promoter<T : Parcelable>(
    initialItems: List<T> = listOf(),
) : BaseNavModel<T, State>(
    screenResolver = PromoterOnScreenResolver,
    finalState = DESTROYED,
    savedStateMap = null
) {

    @Parcelize
    enum class State : Parcelable {
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

    override val initialElements = initialItems.map {
        PromoterElement(
            key = NavKey(it),
            fromState = CREATED,
            targetState = STAGE1,
            operation = Noop()
        )
    }
}
