package com.bumble.appyx.interactions.core

import com.bumble.appyx.NavTarget.Child1
import com.bumble.appyx.NavTarget.Child2
import com.bumble.appyx.NavTarget.Child3
import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.RawValue
import kotlin.test.Test
import kotlin.test.assertEquals

class BaseTransitionModelTest {

    @Test
    fun WHEN_I_enqueue_an_element_THEN_max_progress_should_be_increase() {
        val transitionModel = TestTransitionModel(
            initialElements = listOf(Child1)
        )

        assertEquals(1f, transitionModel.maxProgress)

        transitionModel.operation(TestOperation(Child2))

        assertEquals(2f, transitionModel.maxProgress)
    }

    @Test
    fun WHEN_I_remove_an_element_THEN_max_progress_should_decrease() {
        val transitionModel = TestTransitionModel(
            initialElements = listOf(Child1)
        )

        transitionModel.operation(TestOperation(Child2))

        assertEquals(2f, transitionModel.maxProgress)

        transitionModel.dropAfter(1)

        assertEquals(1f, transitionModel.maxProgress)
    }

    @Test
    fun GIVEN_progress_is_update_WHEN_it_is_smaller_than_its_max_THEN_it_should_have_the_same_segment() {
        val transitionModel = TestTransitionModel(
            initialElements = listOf(Child1)
        )

        transitionModel.operation(TestOperation(Child2))
        transitionModel.operation(TestOperation(Child3))

        transitionModel.setProgress(1.5f)

        assertEquals(1, transitionModel.segments.value.index)
    }

    @Test
    fun GIVEN_progress_is_update_WHEN_it_is_equal_to_its_max_THEN_it_should_have_the_next_segment() {
        val transitionModel = TestTransitionModel(
            initialElements = listOf(Child1)
        )

        transitionModel.operation(TestOperation(Child2))
        transitionModel.operation(TestOperation(Child3))

        transitionModel.setProgress(2f)

        assertEquals(2, transitionModel.segments.value.index)
    }

    @Test
    fun GIVEN_progress_is_update_beyond_max_progress_THEN_it_should_reach_the_end() {
        val transitionModel = TestTransitionModel(
            initialElements = listOf(Child1)
        )

        transitionModel.operation(TestOperation(Child2))
        transitionModel.operation(TestOperation(Child3))

        transitionModel.setProgress(100f)

        assertEquals(2, transitionModel.segments.value.index)
    }

    private class TestTransitionModel<NavTarget : Any>(
        initialElements: List<NavTarget>,
    ) : BaseTransitionModel<NavTarget, TestTransitionModel.State<NavTarget>>() {
        data class State<NavTarget>(val elements: List<NavElement<NavTarget>>)

        override val initialState: State<NavTarget> = State(
            elements = initialElements.map { it.asElement() }
        )

        override fun State<NavTarget>.destroyedElements(): Set<NavElement<NavTarget>> = setOf()

        override fun State<NavTarget>.availableElements(): Set<NavElement<NavTarget>> = setOf()
    }

    @Parcelize
    private class TestOperation<NavTarget : Any>(
        private val navTarget: @RawValue NavTarget,
        override val mode: Operation.Mode = Operation.Mode.KEYFRAME
    ) : BaseOperation<TestTransitionModel.State<NavTarget>>() {
        override fun createFromState(baseLineState: TestTransitionModel.State<NavTarget>): TestTransitionModel.State<NavTarget> =
            baseLineState

        override fun createTargetState(fromState: TestTransitionModel.State<NavTarget>): TestTransitionModel.State<NavTarget> =
            fromState.copy(elements = fromState.elements + navTarget.asElement())

        override fun isApplicable(state: TestTransitionModel.State<NavTarget>): Boolean = true
    }
}
