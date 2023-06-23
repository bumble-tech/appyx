package com.bumble.appyx.transitionmodel

import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.core.ui.context.TestUiContext
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.interactions.core.ui.state.BaseMutableUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class KeyframeStepsTest {

    @Test
    fun WHEN_non_monotonic_sequence_of_steps_is_provided_THEN_exception_is_thrown() {
        assertFailsWith<IllegalArgumentException> {
            keyframeSteps(
                -1f to TargetUiState(Scale.Target(-1f)),
                0f to TargetUiState(Scale.Target(0f)),
                2.1f to TargetUiState(Scale.Target(2.1f)),
                2f to TargetUiState(Scale.Target(2f)),
                effectiveIndexAccessor = { it.scale.value },
                calculateEffectiveIndex = { axisValue, _ -> axisValue },
            )
        }
    }

    @Test
    fun GIVEN_valid_keyframe_steps_WHEN_querying_from_step_THEN_expected_step_is_returned() {
        val keyframeSteps = keyframeSteps()
        val fromStep = keyframeSteps.getFromStep(0.3f)
        assertEquals(0f, fromStep.step)
        assertEquals(0.25f, fromStep.targetUiState.scale.value)
    }

    @Test
    fun GIVEN_valid_keyframe_steps_WHEN_querying_from_step_with_fraction_value_bellow_step_range_THEN_expected_step_is_returned() {
        val keyframeSteps = keyframeSteps()
        val fromStep = keyframeSteps.getFromStep(-2f)
        assertEquals(-1f, fromStep.step)
        assertEquals(0f, fromStep.targetUiState.scale.value)
    }

    @Test
    fun GIVEN_valid_keyframe_steps_WHEN_querying_from_step_with_fraction_value_above_step_range_THEN_expected_step_is_returned() {
        val keyframeSteps = keyframeSteps()
        val fromStep = keyframeSteps.getFromStep(3f)
        assertEquals(2f, fromStep.step)
        assertEquals(0.75f, fromStep.targetUiState.scale.value)
    }

    @Test
    fun GIVEN_valid_keyframe_steps_WHEN_querying_to_step_THEN_expected_step_is_returned() {
        val keyframeSteps = keyframeSteps()
        val fromStep = keyframeSteps.getToStep(0.3f)
        assertEquals(1f, fromStep.step)
        assertEquals(1f, fromStep.targetUiState.scale.value)
    }

    @Test
    fun GIVEN_valid_keyframe_steps_WHEN_querying_to_step_with_fraction_value_bellow_step_range_THEN_expected_step_is_returned() {
        val keyframeSteps = keyframeSteps()
        val fromStep = keyframeSteps.getToStep(-2f)
        assertEquals(-1f, fromStep.step)
        assertEquals(0f, fromStep.targetUiState.scale.value)
    }

    @Test
    fun GIVEN_valid_keyframe_steps_WHEN_querying_to_step_with_fraction_value_above_step_range_THEN_expected_step_is_returned() {
        val keyframeSteps = keyframeSteps()
        val fromStep = keyframeSteps.getToStep(3f)
        assertEquals(2f, fromStep.step)
        assertEquals(0.75f, fromStep.targetUiState.scale.value)
    }

    @Test
    fun GIVEN_valid_keyframe_steps_WHEN_lerping_value_inside_steps_range_THEN_target_ui_state_is_interpolated() {
        val keyframeSteps = keyframeSteps()
        val targetUiState = keyframeSteps.lerpTargetUiState(fraction = 0.2f) { from, to, fraction ->
            lerp(from, to, fraction)
        }
        assertNotNull(targetUiState)
        assertEquals(0.4f, targetUiState.scale.value)
    }

    @Test
    fun GIVEN_valid_keyframe_steps_WHEN_lerping_value_below_steps_range_THEN_target_ui_state_is_interpolated() {
        val uiContext: UiContext = TestUiContext
        val keyframeSteps = keyframeSteps()
        val targetUiState = keyframeSteps.lerpTargetUiState(fraction = -2f) { from, to, fraction ->
            lerp(from, to, fraction)
        }
        assertNotNull(targetUiState)
        assertEquals(0f, targetUiState.scale.value)
    }

    @Test
    fun GIVEN_valid_keyframe_steps_WHEN_lerping_value_above_steps_range_THEN_target_ui_state_is_interpolated() {
        val uiContext: UiContext = TestUiContext
        val keyframeSteps = keyframeSteps()
        val targetUiState = keyframeSteps.lerpTargetUiState(fraction = 3f) { from, to, fraction ->
            lerp(from, to, fraction)
        }
        assertNotNull(targetUiState)
        assertEquals(0.75f, targetUiState.scale.value)
    }


    private fun keyframeSteps() =
        keyframeSteps(
            -1f to TargetUiState(Scale.Target(0f)),
            0f to TargetUiState(Scale.Target(0.25f)),
            1f to TargetUiState(Scale.Target(1f)),
            2f to TargetUiState(Scale.Target(0.75f)),
            effectiveIndexAccessor = { it.scale.value },
            calculateEffectiveIndex = { axisValue, _ -> axisValue }
        )

    private class TargetUiState(
        val scale: Scale.Target
    )

    private fun lerp(from: TargetUiState, to: TargetUiState, fraction: Float) =
        TargetUiState(
            scale = from.scale.lerpTo(to.scale, fraction),
        )

    private class MutableUiState(
        uiContext: UiContext,
        val scale: Scale,
    ) : BaseMutableUiState<TargetUiState>(
        uiContext = uiContext,
        motionProperties = listOf(scale),
    ) {
        override val modifier: Modifier = Modifier
            .then(scale.modifier)

        override suspend fun animateTo(
            scope: CoroutineScope,
            target: TargetUiState,
            springSpec: SpringSpec<Float>,
        ) {
            listOf(
                scope.async {
                    scale.animateTo(
                        target.scale.value,
                        spring(springSpec.dampingRatio, springSpec.stiffness),
                    )
                },
            ).awaitAll()
        }

        override suspend fun snapTo(target: TargetUiState) {
            scale.snapTo(target.scale.value)
        }

        override fun lerpTo(
            scope: CoroutineScope,
            start: TargetUiState,
            end: TargetUiState,
            fraction: Float,
        ) {
            scope.launch {
                scale.lerpTo(start.scale, end.scale, fraction)
            }
        }
    }

}
