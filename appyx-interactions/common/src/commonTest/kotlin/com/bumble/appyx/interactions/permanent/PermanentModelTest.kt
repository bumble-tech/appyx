package com.bumble.appyx.interactions.permanent

import com.bumble.appyx.InteractionTarget
import com.bumble.appyx.interactions.core.state.MutableSavedStateMapImpl
import com.bumble.appyx.interactions.permanent.operation.AddUnique
import kotlin.test.Test
import kotlin.test.assertEquals

class PermanentModelTest {

    @Test
    fun GIVEN_permanent_model_with_savedState_WHEN_created_THEN_restores_state_correctly() {
        val savedStateMap = mutableMapOf<String, Any?>()
        val permanentModel = PermanentModel<InteractionTarget>(
            savedStateMap = savedStateMap
        )

        permanentModel.operation(AddUnique(InteractionTarget.Child1))

        val state = MutableSavedStateMapImpl(savedStateMap) { true }
        permanentModel.saveInstanceState(state)

        val newPermanentModel = PermanentModel<InteractionTarget>(savedStateMap = state.savedState)

        assertEquals(
            listOf(InteractionTarget.Child1),
            newPermanentModel.output.value.currentTargetState.elements.map { it.interactionTarget }
        )
    }

    @Test
    fun GIVEN_permanent_model_without_savedState_WHEN_created_THEN_state_equals_initial_targets() {
        val initialTargets = listOf(InteractionTarget.Child2, InteractionTarget.Child3)

        val newPermanentModel = PermanentModel(
            initialTargets = initialTargets,
            savedStateMap = null
        )

        assertEquals(
            initialTargets,
            newPermanentModel.output.value.currentTargetState.elements.map { it.interactionTarget }
        )
    }
}
