package com.bumble.appyx.navmodel.cards

import android.os.Parcelable
import com.bumble.appyx.core.navigation.BaseNavModel
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.core.navigation.Operation.Noop
import com.bumble.appyx.navmodel.cards.Cards.State
import com.bumble.appyx.navmodel.cards.Cards.State.Bottom
import com.bumble.appyx.navmodel.cards.Cards.State.IndicateLike
import com.bumble.appyx.navmodel.cards.Cards.State.IndicatePass
import com.bumble.appyx.navmodel.cards.Cards.State.Queued
import com.bumble.appyx.navmodel.cards.Cards.State.Top
import com.bumble.appyx.navmodel.cards.Cards.State.VoteLike
import com.bumble.appyx.navmodel.cards.Cards.State.VotePass
import kotlinx.parcelize.Parcelize

class Cards<NavTarget : Any>(
    initialItems: List<NavTarget> = listOf(),
) : BaseNavModel<NavTarget, State>(
    screenResolver = CardsOnScreenResolver,
    finalStates = FINAL_STATES,
    savedStateMap = null
) {
    companion object {
        internal val FINAL_STATES = setOf(VoteLike, VotePass)
        internal val TOP_STATES = setOf(Top, IndicateLike, IndicatePass)
    }

    sealed class State : Parcelable {
        @Parcelize
        data class Queued(val queueNumber: Int) : State()
        @Parcelize
        object Bottom : State()
        @Parcelize
        object Top : State()
        @Parcelize
        object IndicateLike : State()
        @Parcelize
        object IndicatePass : State()
        @Parcelize
        object VoteLike : State()
        @Parcelize
        object VotePass : State()

        fun next(): State =
            when (this) {
                is Queued -> if (queueNumber == 0) Bottom else Queued(queueNumber - 1)
                is Bottom -> Top
                else -> this
            }
    }

    override val initialElements = initialItems.mapIndexed { index, element ->
        val state = when (index) {
            0 -> Top
            1 -> Bottom
            else -> Queued(index - 2)
        }
        CardsElement(
            key = NavKey(element),
            fromState = if (state == Top) Bottom else state,
            targetState = state,
            operation = Noop()
        )
    }
}
