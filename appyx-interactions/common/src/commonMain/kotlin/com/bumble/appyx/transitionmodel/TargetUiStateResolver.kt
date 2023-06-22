package com.bumble.appyx.transitionmodel

import com.bumble.appyx.interactions.AppyxLogger
import com.bumble.appyx.interactions.core.ui.math.lerpFloat
import com.bumble.appyx.interactions.core.ui.property.impl.GenericFloatProperty

data class LerpInfo<TargetUiState>(
    val from: TargetUiState,
    val to: TargetUiState,
    val fraction: Float,
)

interface TargetUiStateResolver<TargetUiState, MutableUiState> {

    fun resolveLerpInfo(from: TargetUiState, to: TargetUiState, progress: Float): LerpInfo<TargetUiState>

    companion object {
        fun <ModelState, TargetUiState, MutableUiState> List<ViewpointType<ModelState, Any?>>.infer(): TargetUiStateResolver<TargetUiState, MutableUiState> =
            when {
                isValidForKeyframe() -> KeyframeTargetUiStateResolver(extractKeyframeStepsList())
                isValidForDelegated() -> DelegatedTargetUiStateResolver()
                else -> throw IllegalArgumentException(resolveExceptionMessage())
            }

        private fun <ModelState> List<ViewpointType<ModelState, Any?>>.isValidForKeyframe(): Boolean =
            size == 1 && all { it.third != null && it.third is KeyframeSteps<*> }

        @Suppress("UNCHECKED_CAST")
        private fun <ModelState, TargetUiState> List<ViewpointType<ModelState, Any?>>.extractKeyframeStepsList() =
            map { it.second to it.third as KeyframeSteps<TargetUiState> }.first()

        private fun <ModelState> List<ViewpointType<ModelState, Any?>>.isValidForDelegated() =
            all { it.third == null }

        private fun <ModelState> List<ViewpointType<ModelState, Any?>>.resolveExceptionMessage() =
            when {
                size > 1 -> "Support for multiple viewpoint dimensions isn't still implemented"
                else -> "Mixing keyframe and non-keyframe based dimensions is not supported."
            }
    }
}

private class KeyframeTargetUiStateResolver<TargetUiState, MutableUiState>(
    private val viewpointDimension: Pair<GenericFloatProperty, KeyframeSteps<TargetUiState>>,
) : TargetUiStateResolver<TargetUiState, MutableUiState> {

    override fun resolveLerpInfo(from: TargetUiState, to: TargetUiState, progress: Float): LerpInfo<TargetUiState> {
        val keyframeSteps = viewpointDimension.second
        val fromIndex = keyframeSteps.effectiveIndexAccessor(from)
        val toIndex = keyframeSteps.effectiveIndexAccessor(to)
        val stepProgress = lerpFloat(fromIndex, toIndex, progress)
        val fromStep = keyframeSteps.getFromStep(stepProgress)
        val toStep = keyframeSteps.getToStep(stepProgress)
        val stepsDiff = toStep.step - fromStep.step
        AppyxLogger.d("resolveLerpInfo", "segmentFrom -> ${keyframeSteps.effectiveIndexAccessor(from)}")
        AppyxLogger.d("resolveLerpInfo", "segmentTo -> ${keyframeSteps.effectiveIndexAccessor(to)}")
        AppyxLogger.d("resolveLerpInfo", "progress -> $progress")
        AppyxLogger.d("resolveLerpInfo", "stepProgress -> $stepProgress")
        AppyxLogger.d("resolveLerpInfo", "fromStep -> ${fromStep.step}")
        AppyxLogger.d("resolveLerpInfo", "toStep -> ${toStep.step}")
        AppyxLogger.d("resolveLerpInfo", "stepsDiff -> $stepsDiff")
        return if (stepsDiff != 0f) {
            val lerpFraction = (stepProgress - fromStep.step) / stepsDiff
            AppyxLogger.d("resolveLerpInfo", "lerpFraction -> $lerpFraction")
            LerpInfo(
                from = fromStep.inferTargetUiState(stepProgress),
                to = toStep.inferTargetUiState(stepProgress),
                fraction = lerpFraction,
            )
        } else {
            LerpInfo(
                from = fromStep.inferTargetUiState(fromStep.step),
                to = fromStep.inferTargetUiState(fromStep.step),
                fraction = 0f,
            )
        }
    }
}

private class DelegatedTargetUiStateResolver<TargetUiState, MutableUiState> : TargetUiStateResolver<TargetUiState, MutableUiState> {
    override fun resolveLerpInfo(from: TargetUiState, to: TargetUiState, progress: Float): LerpInfo<TargetUiState> =
        LerpInfo(
            from = from,
            to = to,
            fraction = progress,
        )
}
