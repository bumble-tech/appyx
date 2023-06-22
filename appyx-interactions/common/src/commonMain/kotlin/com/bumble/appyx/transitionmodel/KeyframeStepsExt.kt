package com.bumble.appyx.transitionmodel

import com.bumble.appyx.interactions.AppyxLogger

internal fun <TargetUiState> List<KeyframeStep<TargetUiState>>.validateSteps() {
    if (hasNonMonotonicSequence()) {
        throw IllegalArgumentException("Keyframe steps should be increase monotonically.")
    }
}

private fun <TargetUiState> List<KeyframeStep<TargetUiState>>.hasNonMonotonicSequence() =
    windowed(2, 1).any { (prev, cur) -> prev.step >= cur.step }

fun <TargetUiState> KeyframeSteps<TargetUiState>.getFromStep(fraction: Float): KeyframeStep<TargetUiState> =
    steps.lastOrNull { it.step <= fraction } ?: steps.first()

fun <TargetUiState> KeyframeSteps<TargetUiState>.getFromTargetUiState(scroll: Float, index: Float): TargetUiState =
    calculateEffectiveIndex(scroll, index).let { fraction ->
        getFromStep(fraction).inferTargetUiState(fraction)
    }

fun <TargetUiState> KeyframeSteps<TargetUiState>.getToStep(fraction: Float): KeyframeStep<TargetUiState> =
    steps.firstOrNull { fraction < it.step } ?: steps.last()

fun <TargetUiState> KeyframeSteps<TargetUiState>.lerpTargetUiState(
    fraction: Float,
    lerpTargetUiState: (TargetUiState, TargetUiState, Float) -> TargetUiState,
): TargetUiState {
    val fromStep = getFromStep(fraction)
    val toStep = getToStep(fraction)
    val stepsDiff = toStep.step - fromStep.step
    return if (stepsDiff != 0f) {
        val lerpFraction = (fraction - fromStep.step) / stepsDiff
        if (lerpFraction != 0f && lerpFraction != 1f) {
            val from = fromStep.inferTargetUiState(fraction)
            val to = toStep.inferTargetUiState(fraction)
            lerpTargetUiState(from, to, lerpFraction).also {
                AppyxLogger.d("KeyframeSteps", "lerpFraction -> $lerpFraction")
                AppyxLogger.d("KeyframeSteps", "from -> $from")
                AppyxLogger.d("KeyframeSteps", "to -> $to")
                AppyxLogger.d("KeyframeSteps", "it -> $it")
            }
        } else {
            fromStep.inferTargetUiState(fraction)
        }
    } else {
        fromStep.inferTargetUiState(fraction)
    }
}
