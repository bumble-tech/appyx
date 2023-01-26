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
    fun `When I enqueue an element Then max progress should be increase`() {
        val backStack = BackStackModel(
            initialTarget = Child1,
            savedStateMap = null
        )

        assertEquals(1f, backStack.maxProgress)

        backStack.enqueue(Push(Child2))

        assertEquals(2f, backStack.maxProgress)
    }

    @Test
    fun `When I remove an element Then max progress should decrease`() {
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
    fun `Given progress is update When it's smaller than it's max Then it should have the same segment`() {
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
    fun `Given progress is update When it's equal to it's max Then it should have the next segment`() {
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
    fun `Given progress is update beyond max progress Then it should reach the end`() {
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
