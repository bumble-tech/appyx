package com.bumble.appyx.components.demos.cards.restore

import com.bumble.appyx.components.demos.InteractionTarget
import com.bumble.appyx.components.demos.cards.CardsModel
import com.bumble.appyx.components.demos.cards.operation.VotePass
import com.bumble.appyx.interactions.core.state.MutableSavedStateMapImpl
import kotlin.test.Test
import kotlin.test.assertEquals

class CardsRestoreTest {

    @Test
    fun GIVEN_2_cards_WHEN_second_card_on_top_AND_state_restored_THEN_second_card_is_on_top() {
        val savedStateMap = mutableMapOf<String, Any?>()
        val cards = CardsModel(
            initialItems = listOf(InteractionTarget.Child1, InteractionTarget.Child2),
            savedStateMap = savedStateMap
        )

        cards.operation(VotePass())

        val state = MutableSavedStateMapImpl(savedStateMap) { true }
        cards.saveInstanceState(state)

        val newCards = CardsModel(
            initialItems = listOf(InteractionTarget.Child1, InteractionTarget.Child2),
            savedStateMap = state.savedState
        )

        assertEquals(
            cards.output.value.currentTargetState,
            newCards.output.value.currentTargetState
        )
    }

}
