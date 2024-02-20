package com.bumble.appyx.components.stable.backstack.restore

import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.stable.backstack.TestTarget.Child1
import com.bumble.appyx.components.stable.backstack.TestTarget.Child2
import com.bumble.appyx.components.backstack.operation.Push
import com.bumble.appyx.interactions.state.MutableSavedStateMapImpl
import kotlin.test.Test
import kotlin.test.assertEquals

class BackstackRestoreTest {

    @Test
    fun GIVEN_backstack_with_2_elements_WHEN_state_restored_THEN_all_elements_are_retained() {
        val savedStateMap = mutableMapOf<String, Any?>()
        val backstack = BackStackModel(
            initialTargets = listOf(Child1),
            savedStateMap = savedStateMap
        )

        backstack.operation(Push(Child2))

        val state = MutableSavedStateMapImpl(savedStateMap) { true }
        backstack.saveInstanceState(state)

        val newBackstack = BackStackModel(
            initialTargets = listOf(Child1),
            savedStateMap = state.savedState
        )

        assertEquals(
            backstack.output.value.currentTargetState,
            newBackstack.output.value.currentTargetState
        )
    }
}
