package com.bumble.appyx.transitionmodel

data class KeyframeStep<TargetUiState>(
    val step: Float,
    val inferTargetUiState: (Float) -> TargetUiState,
)

data class KeyframeSteps<TargetUiState>(
    val steps: List<KeyframeStep<TargetUiState>>,
    val effectiveIndexAccessor: (TargetUiState) -> Float,
    val calculateEffectiveIndex: (Float, Float) -> Float,
)

fun <TargetUiState> keyframeSteps(
    vararg steps: Pair<Float, (Float) -> TargetUiState>,
    effectiveIndexAccessor: (TargetUiState) -> Float,
    calculateEffectiveIndex: (Float, Float) -> Float = { scroll, index -> (index - scroll) },
) = KeyframeSteps(
    steps = steps.map { KeyframeStep(it.first, it.second) }.apply { validateSteps() },
    effectiveIndexAccessor = effectiveIndexAccessor,
    calculateEffectiveIndex = calculateEffectiveIndex,
)
