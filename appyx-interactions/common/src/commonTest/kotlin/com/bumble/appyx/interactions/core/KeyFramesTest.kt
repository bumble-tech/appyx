package com.bumble.appyx.interactions.core

import com.bumble.appyx.InteractionTarget
import com.bumble.appyx.InteractionTarget.Child1
import com.bumble.appyx.InteractionTarget.Child2
import com.bumble.appyx.InteractionTarget.Child3
import com.bumble.appyx.interactions.core.TestTransitionModel.State
import com.bumble.appyx.interactions.core.model.transition.Keyframes
import com.bumble.appyx.interactions.core.model.transition.StateTransition
import com.bumble.appyx.interactions.core.model.transition.Segment
import com.bumble.appyx.interactions.core.model.transition.Update
import com.bumble.appyx.interactions.core.model.transition.toSegmentProgress
import kotlin.test.Test
import kotlin.test.assertEquals

class KeyFramesTest {

    @Test
    fun WHEN_a_derived_frame_is_created_THEN_max_progress_should_increase_by_1() {
        val keyFrames = Keyframes(
            queue = listOf<Segment<State<InteractionTarget>>>()
        )

        val state = State<InteractionTarget>(listOf())

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
                    StateTransition(
                        fromState = State(listOf()),
                        targetState = State(listOf(asElement()))
                    )
                ),
                Segment(
                    StateTransition(
                        fromState = State(listOf(asElement())),
                        targetState = State(listOf(asElement(), asElement()))
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
            StateTransition(
                fromState = State(listOf()),
                targetState = State(listOf(asElement()))
            )
        )
        val keyFrames = Keyframes(
            queue = listOf(
                firstSegment,
                Segment(
                    StateTransition(
                        fromState = State(listOf(asElement())),
                        targetState = State(listOf(asElement(), asElement()))
                    )
                )
            )
        )

        keyFrames.setProgress(0.5f) {}

        assertEquals(firstSegment, keyFrames.currentSegment)
        assertEquals(0, keyFrames.currentIndex)
    }

    @Test
    fun GIVEN_there_are_2_segments_WHEN_the_progress_is_at_the_end_THEN_currentSegment_will_be_the_last() {
        val secondSegment = Segment(
            StateTransition(
                fromState = State(listOf(asElement())),
                targetState = State(listOf(asElement(), asElement()))
            )
        )
        val keyFrames = Keyframes(
            queue = listOf(
                Segment(
                    StateTransition(
                        fromState = State(listOf()),
                        targetState = State(listOf(asElement()))
                    )
                ),
                secondSegment
            )
        )

        keyFrames.setProgress(1f) {}


        assertEquals(secondSegment, keyFrames.currentSegment)
        assertEquals(1, keyFrames.currentIndex)
    }


    @Test
    fun WHEN_progress_is_set_beyond_maximum_THEN_it_is_capped_at_maximum() {
        val secondSegment = Segment(
            StateTransition(
                fromState = State(listOf(asElement())),
                targetState = State(listOf(asElement(), asElement()))
            )
        )
        val keyFrames = Keyframes(
            queue = listOf(
                Segment(
                    StateTransition(
                        fromState = State(listOf()),
                        targetState = State(listOf(asElement()))
                    )
                ),
                secondSegment
            )
        )

        keyFrames.setProgress(3f) {}


        assertEquals(secondSegment, keyFrames.currentSegment)
        assertEquals(1, keyFrames.currentIndex)
    }

    @Test
    fun WHEN_progress_is_set_before_minimum_THEN_it_is_capped_at_minimum() {
        val firstSegment = Segment(
            StateTransition(
                fromState = State(listOf()),
                targetState = State(listOf(asElement()))
            )
        )
        val keyFrames = Keyframes(
            queue = listOf(
                firstSegment,
                Segment(
                    StateTransition(
                        fromState = State(listOf(asElement())),
                        targetState = State(listOf(asElement(), asElement()))
                    )
                )

            )
        )

        keyFrames.setProgress(-1f) {}

        assertEquals(firstSegment, keyFrames.currentSegment)
        assertEquals(0, keyFrames.currentIndex)
    }

    @Test
    fun WHEN_update_THEN_new_update_is_created() {
        val segmentFromState = State(listOf(Child1.asElement()))
        val segmentTargetState = State(listOf(Child1.asElement(), Child2.asElement()))
        val keyFrames = Keyframes(
            queue = listOf(
                Segment(
                    StateTransition(
                        fromState = segmentFromState,
                        targetState = segmentTargetState
                    )
                )
            )
        )

        val fromState = State<InteractionTarget>(listOf())
        val targetState = State(listOf(Child3.asElement()))
        val stateTransition = StateTransition(
            fromState = fromState,
            targetState = targetState
        )

        val update = keyFrames.deriveUpdate(stateTransition)

        val expected = Update(
            currentTargetState = targetState
        )

        assertEquals(expected, update)
    }

    @Test
    fun GIVEN_on_the_last_segment_WHEN_progress_is_max_THEN_segment_progress_is_1() {
        val keyFrames = Keyframes(
            queue = listOf(
                Segment(
                    StateTransition(
                        fromState = State(listOf()),
                        targetState = State(listOf(asElement()))
                    )
                ),
                Segment(
                    StateTransition(
                        fromState = State(listOf(asElement())),
                        targetState = State(listOf(asElement(), asElement()))
                    )
                )
            )
        )

        keyFrames.setProgress(2f) {}

        assertEquals(1, keyFrames.currentIndex)
        assertEquals(1f, keyFrames.progress.toSegmentProgress(keyFrames.currentIndex))
    }

    @Test
    fun WHEN_on_the_last_segment_THEN_segment_progress_is_0() {
        val keyFrames = Keyframes(
            queue = listOf(
                Segment(
                    StateTransition(
                        fromState = State(listOf()),
                        targetState = State(listOf(asElement()))
                    )
                ),
                Segment(
                    StateTransition(
                        fromState = State(listOf(asElement())),
                        targetState = State(listOf(asElement(), asElement()))
                    )
                )
            )
        )

        keyFrames.setProgress(1f) {}

        assertEquals(0f, keyFrames.progress.toSegmentProgress(keyFrames.currentIndex))
        assertEquals(1, keyFrames.currentIndex)
    }
}
