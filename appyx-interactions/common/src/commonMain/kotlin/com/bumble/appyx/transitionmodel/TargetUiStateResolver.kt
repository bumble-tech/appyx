package com.bumble.appyx.transitionmodel

import com.bumble.appyx.interactions.core.ui.math.lerpFloat
import com.bumble.appyx.interactions.core.ui.property.impl.GenericFloatProperty

data class LerpInfo<TargetUiState>(
    val from: TargetUiState,
    val to: TargetUiState,
    val fraction: Float,
)

interface TargetUiStateResolver<TargetUiState, MutableUiState> {

    fun resolveLerpInfo(segmentFrom: TargetUiState, segmentTo: TargetUiState, progress: Float): LerpInfo<TargetUiState>

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

    override fun resolveLerpInfo(segmentFrom: TargetUiState, segmentTo: TargetUiState, progress: Float): LerpInfo<TargetUiState> {
        val keyframeSteps = viewpointDimension.second
        val fromIndex = keyframeSteps.effectiveIndexAccessor(segmentFrom)
        val toIndex = keyframeSteps.effectiveIndexAccessor(segmentTo)
        val stepProgress = lerpFloat(fromIndex, toIndex, progress)
        return keyframeSteps.lerpTargetUiState(
            fraction = stepProgress,
            mapWithLerp = { from, to, lerpFraction -> LerpInfo(from, to, lerpFraction) },
            mapFrom = { from -> LerpInfo(from = from, to = from, fraction = 0f) }
        )
    }
}

private class DelegatedTargetUiStateResolver<TargetUiState, MutableUiState> : TargetUiStateResolver<TargetUiState, MutableUiState> {
    override fun resolveLerpInfo(segmentFrom: TargetUiState, segmentTo: TargetUiState, progress: Float): LerpInfo<TargetUiState> =
        LerpInfo(
            from = segmentFrom,
            to = segmentTo,
            fraction = progress,
        )
}
