package com.bumble.appyx.transitionmodel.cards

import com.bumble.appyx.interactions.core.BaseTransitionModel
import com.bumble.appyx.interactions.core.NavKey
import com.bumble.appyx.interactions.core.Operation

class Cards<NavTarget : Any>(
    initialItems: List<NavTarget> = listOf(),
) : BaseTransitionModel<NavTarget, Cards.State>(
    finalStates = FINAL_STATES,
) {
    companion object {
        internal val FINAL_STATES = setOf(State.VoteLike, State.VotePass)
        internal val TOP_STATES = setOf(State.Top)
    }

    sealed class State {
        data class Queued(val queueNumber: Int) : State()
        object Bottom : State()
        object Top : State()
        object VoteLike : State()
        object VotePass : State()

        fun next(): State =
            when (this) {
                is Queued -> if (queueNumber == 0) Bottom else Queued(queueNumber - 1)
                is Bottom -> Top
                else -> this
            }
    }

    override val initialState = initialItems.mapIndexed { index, element ->
        val state = when (index) {
            0 -> State.Top
            1 -> State.Bottom
            else -> State.Queued(index - 2)
        }
        CardsElement(
            key = NavKey(element),
            fromState = if (state == State.Top) State.Bottom else state,
            targetState = state,
            operation = Operation.Noop()
        )
    }
}
