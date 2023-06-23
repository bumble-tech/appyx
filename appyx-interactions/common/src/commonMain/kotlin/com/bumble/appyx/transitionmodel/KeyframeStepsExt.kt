package com.bumble.appyx.transitionmodel

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
    calculateEffectiveIndex(scroll, index).let { fraction -> getFromStep(fraction).targetUiState }

fun <TargetUiState> KeyframeSteps<TargetUiState>.getToStep(fraction: Float): KeyframeStep<TargetUiState> =
    steps.firstOrNull { fraction < it.step } ?: steps.last()

fun <TargetUiState> KeyframeSteps<TargetUiState>.lerpTargetUiState(
    fraction: Float,
    lerpTargetUiState: (TargetUiState, TargetUiState, Float) -> TargetUiState,
): TargetUiState =
    lerpTargetUiState(
        fraction = fraction,
        mapWithLerp = { from, to, lerpFraction -> lerpTargetUiState(from, to, lerpFraction) },
        mapFrom = { from -> from }
    )

fun <TargetUiState, T> KeyframeSteps<TargetUiState>.lerpTargetUiState(
    fraction: Float,
    mapWithLerp: (from: TargetUiState, to: TargetUiState, lerpFraction: Float) -> T,
    mapFrom: (from: TargetUiState) -> T,
): T {
    val fromStep = getFromStep(fraction)
    val toStep = getToStep(fraction)
    val stepsDiff = toStep.step - fromStep.step
    return if (stepsDiff != 0f) {
        val lerpFraction = (fraction - fromStep.step) / stepsDiff
        mapWithLerp(fromStep.targetUiState, toStep.targetUiState, lerpFraction)
    } else {
        mapFrom(fromStep.targetUiState)
    }
}
