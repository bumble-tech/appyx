package com.bumble.appyx.interactions.core

import com.bumble.appyx.interactions.core.BaseTransitionModelTest.Companion.NavTarget.Child1
import com.bumble.appyx.interactions.core.BaseTransitionModelTest.Companion.NavTarget.Child2
import com.bumble.appyx.interactions.core.BaseTransitionModelTest.Companion.NavTarget.Child3
import com.bumble.appyx.transitionmodel.backstack.BackStackModel
import com.bumble.appyx.transitionmodel.backstack.operation.Push
import kotlin.test.Test
import kotlin.test.assertEquals

class BaseTransitionModelTest {

    @Test
    fun WHEN_I_enqueue_an_element_THEN_max_progress_should_be_increase() {
        val backStack = BackStackModel(
            initialTarget = Child1,
            savedStateMap = null
        )

        assertEquals(1f, backStack.maxProgress)

        backStack.enqueue(Push(Child2))

        assertEquals(2f, backStack.maxProgress)
    }

    @Test
    fun WHEN_I_remove_an_element_THEN_max_progress_should_decrease() {
        val backStack = BackStackModel(
            initialTarget = Child1,
            savedStateMap = null
        )
        backStack.enqueue(Push(Child2))

        assertEquals(2f, backStack.maxProgress)

        backStack.dropAfter(1)

        assertEquals(1f, backStack.maxProgress)
    }

    @Test
    fun GIVEN_progress_is_update_WHEN_it_is_smaller_than_its_max_THEN_it_should_have_the_same_segment() {
        val backStack = BackStackModel(
            initialTarget = Child1,
            savedStateMap = null
        )
        backStack.enqueue(Push(Child2))
        backStack.enqueue(Push(Child3))

        backStack.setProgress(1.5f)

        assertEquals(1, backStack.segments.value.index)
    }

    @Test
    fun GIVEN_progress_is_update_WHEN_it_is_equal_to_its_max_THEN_it_should_have_the_next_segment() {
        val backStack = BackStackModel(
            initialTarget = Child1,
            savedStateMap = null
        )
        backStack.enqueue(Push(Child2))
        backStack.enqueue(Push(Child3))

        backStack.setProgress(2f)

        assertEquals(2, backStack.segments.value.index)
    }

    @Test
    fun GIVEN_progress_is_update_beyond_max_progress_THEN_it_should_reach_the_end() {
        val backStack = BackStackModel(
            initialTarget = Child1,
            savedStateMap = null
        )
        backStack.enqueue(Push(Child2))
        backStack.enqueue(Push(Child3))

        backStack.setProgress(100f)

        assertEquals(2, backStack.segments.value.index)
    }

    private companion object {
        enum class NavTarget {
            Child1, Child2, Child3
        }
    }
}
