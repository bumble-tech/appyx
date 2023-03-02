package com.bumble.appyx.interactions.core

import com.bumble.appyx.NavTarget.Child1
import com.bumble.appyx.NavTarget.Child2
import com.bumble.appyx.interactions.core.TestTransitionModel.State
import com.bumble.appyx.interactions.core.model.transition.Keyframes
import com.bumble.appyx.interactions.core.model.transition.NavTransition
import com.bumble.appyx.interactions.core.model.transition.Segment
import com.bumble.appyx.interactions.core.model.transition.Update
import kotlin.test.Test
import kotlin.test.assertEquals

class UpdateTest {

    @Test
    fun WHEN_replace_THEN_targetState_isUpdated() {
        val update = Update(
            currentTargetState = State(
                elements = listOf(
                    asElement()
                )
            )
        )

        val targetState = State(listOf(Child2.asElement()))

        val newUpdate = update.replace(targetState)

        assertEquals(Update(currentTargetState = targetState), newUpdate)
    }

    @Test
    fun WHEN_derivedUpdate_THEN_targetState_isUpdated() {
        val currentState = State(
            elements = listOf(
                Child1.asElement()
            )
        )

        val update = Update(currentTargetState = currentState)


        val targetState = State(listOf(Child2.asElement()))
        val navTransition = NavTransition(
            fromState = currentState,
            targetState = targetState
        )
        val newUpdate = update.deriveUpdate(navTransition)

        assertEquals(Update(currentTargetState = targetState), newUpdate)
    }

    @Test
    fun WHEN_deriveKeyFrames_THEN_keyFrames_are_created() {
        val currentState = State(
            elements = listOf(
                Child1.asElement()
            )
        )

        val update = Update(currentTargetState = currentState)


        val targetState = State(listOf(Child2.asElement()))
        val navTransition = NavTransition(
            fromState = currentState,
            targetState = targetState
        )
        val newKeyframes = update.deriveKeyframes(navTransition)

        val expected = Keyframes(
            queue = listOf(
                Segment(navTransition = navTransition)
            )
        )
        assertEquals(expected, newKeyframes)
    }

}
