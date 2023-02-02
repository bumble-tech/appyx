package com.bumble.appyx.interactions.core

import com.bumble.appyx.NavTarget
import com.bumble.appyx.NavTarget.Child1
import com.bumble.appyx.NavTarget.Child2
import com.bumble.appyx.NavTarget.Child3
import com.bumble.appyx.interactions.core.TestTransitionModel.State
import kotlin.test.Test
import kotlin.test.assertEquals

class KeyFramesTest {

    @Test
    fun WHEN_a_derived_frame_is_created_THEN_max_progress_should_increase_by_1() {
        val keyFrames = Keyframes(
            queue = listOf<Segment<State<NavTarget>>>()
        )

        val state = State<NavTarget>(listOf())

        assertEquals(0f, keyFrames.maxProgress)

        val newKeyframes = keyFrames.deriveKeyframes(
            TestOperation(Child2).invoke(state)
        )

        assertEquals(1f, newKeyframes.maxProgress)
    }

    @Test
    fun WHEN_the_last_element_is_dropped_THEN_max_progress_should_decrease_by_1() {
        val keyFrames = Keyframes(
            queue = listOf(
                Segment(
                    NavTransition(
                        fromState = State(listOf()),
                        targetState = State(listOf(Child1.asElement()))
                    )
                ),
                Segment(
                    NavTransition(
                        fromState = State(listOf(Child1.asElement())),
                        targetState = State(listOf(Child1.asElement(), Child2.asElement()))
                    )
                )
            )
        )

        assertEquals(2f, keyFrames.maxProgress)
        assertEquals(1f, keyFrames.dropAfter(0).maxProgress)
    }

    @Test
    fun GIVEN_there_are_2_segments_WHEN_the_progress_is_in_between_THEN_currentSegment_will_be_the_first() {
        val firstSegment = Segment(
            NavTransition(
                fromState = State(listOf()),
                targetState = State(listOf(Child1.asElement()))
            )
        )
        val keyFrames = Keyframes(
            queue = listOf(
                firstSegment,
                Segment(
                    NavTransition(
                        fromState = State(listOf(Child1.asElement())),
                        targetState = State(listOf(Child1.asElement(), Child2.asElement()))
                    )
                )
            )
        )

        val newKeyFrames = keyFrames.setProgress(0.5f) {}


        assertEquals(firstSegment, newKeyFrames.currentSegment)
        assertEquals(0, newKeyFrames.currentIndex)
    }

    @Test
    fun GIVEN_there_are_2_segments_WHEN_the_progress_is_at_the_end_THEN_currentSegment_will_be_the_last() {
        val secondSegment = Segment(
            NavTransition(
                fromState = State(listOf(Child1.asElement())),
                targetState = State(listOf(Child1.asElement(), Child2.asElement()))
            )
        )
        val keyFrames = Keyframes(
            queue = listOf(
                Segment(
                    NavTransition(
                        fromState = State(listOf()),
                        targetState = State(listOf(Child1.asElement()))
                    )
                ),
                secondSegment
            )
        )

        val newKeyFrames = keyFrames.setProgress(1f) {}


        assertEquals(secondSegment, newKeyFrames.currentSegment)
        assertEquals(1, newKeyFrames.currentIndex)
    }


    @Test
    fun WHEN_progress_is_set_beyond_maximum_THEN_it_is_capped_at_maximum() {
        val secondSegment = Segment(
            NavTransition(
                fromState = State(listOf(Child1.asElement())),
                targetState = State(listOf(Child1.asElement(), Child2.asElement()))
            )
        )
        val keyFrames = Keyframes(
            queue = listOf(
                Segment(
                    NavTransition(
                        fromState = State(listOf()),
                        targetState = State(listOf(Child1.asElement()))
                    )
                ),
                secondSegment
            )
        )

        val newKeyFrames = keyFrames.setProgress(3f) {}


        assertEquals(secondSegment, newKeyFrames.currentSegment)
        assertEquals(1, newKeyFrames.currentIndex)
    }

    @Test
    fun WHEN_progress_is_set_before_minimum_THEN_it_is_capped_at_minimum() {
        val firstSegment = Segment(
            NavTransition(
                fromState = State(listOf()),
                targetState = State(listOf(Child1.asElement()))
            )
        )
        val keyFrames = Keyframes(
            queue = listOf(
                firstSegment,
                Segment(
                    NavTransition(
                        fromState = State(listOf(Child1.asElement())),
                        targetState = State(listOf(Child1.asElement(), Child2.asElement()))
                    )
                )

            )
        )

        val newKeyFrames = keyFrames.setProgress(-1f) {}

        assertEquals(firstSegment, newKeyFrames.currentSegment)
        assertEquals(0, newKeyFrames.currentIndex)
    }

    @Test
    fun WHEN_replace_THEN_new_targetState() {
        val keyFrames = Keyframes(
            queue = listOf(
                Segment(
                    NavTransition(
                        fromState = State(listOf()),
                        targetState = State(listOf(Child1.asElement()))
                    )
                )
            )
        )

        val newTargetState = State(listOf(Child2.asElement()))

        assertEquals(newTargetState, keyFrames.replace(newTargetState).currentTargetState)
    }

    @Test
    fun WHEN_update_THEN_new_update_is_created() {
        val segmentFromState = State(listOf(Child1.asElement()))
        val segmentTargetState = State(listOf(Child1.asElement(), Child2.asElement()))
        val keyFrames = Keyframes(
            queue = listOf(
                Segment(
                    NavTransition(
                        fromState = segmentFromState,
                        targetState = segmentTargetState
                    )
                )
            )
        )

        val fromState = State<NavTarget>(listOf())
        val targetState = State(listOf(Child3.asElement()))
        val navTransition = NavTransition(
            fromState = fromState,
            targetState = targetState
        )

        val update = keyFrames.deriveUpdate(navTransition)

        val expected = Update(
            history = listOf(
                segmentFromState,
                segmentTargetState,
                fromState
            ),
            currentTargetState = targetState
        )

        assertEquals(expected, update)
    }

    @Test
    fun GIVEN_on_the_last_segment_WHEN_progress_is_max_THEN_segment_progress_is_1() {
        val keyFrames = Keyframes(
            queue = listOf(
                Segment(
                    NavTransition(
                        fromState = State(listOf()),
                        targetState = State(listOf(Child1.asElement()))
                    )
                ),
                Segment(
                    NavTransition(
                        fromState = State(listOf(Child1.asElement())),
                        targetState = State(listOf(Child1.asElement(), Child2.asElement()))
                    )
                )
            )
        )

        val newKeyFrames = keyFrames.setProgress(2f) {}

        assertEquals(1f, newKeyFrames.segmentProgress)
        assertEquals(1, newKeyFrames.currentIndex)
    }

    @Test
    fun GIVEN_3_segments_WHEN_progress_is_2_THEN_segment_progress_is_1() {
        val keyFrames = Keyframes(
            queue = listOf(
                Segment(
                    NavTransition(
                        fromState = State(listOf()),
                        targetState = State(listOf(Child1.asElement()))
                    )
                ),
                Segment(
                    NavTransition(
                        fromState = State(listOf(Child1.asElement())),
                        targetState = State(listOf(Child1.asElement(), Child2.asElement()))
                    )
                )
            )
        )

        val newKeyFrames = keyFrames.setProgress(1f) {}

        assertEquals(0f, newKeyFrames.segmentProgress)
        assertEquals(1, newKeyFrames.currentIndex)
    }
}
