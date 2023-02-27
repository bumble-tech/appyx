package com.bumble.appyx.interactions.core.ui

import androidx.compose.animation.core.SpringSpec
import com.bumble.appyx.interactions.core.Keyframes
import com.bumble.appyx.interactions.core.Segment
import com.bumble.appyx.interactions.core.TransitionModel
import com.bumble.appyx.interactions.core.Update
import com.bumble.appyx.interactions.core.toSegmentProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

interface Interpolator<NavTarget, ModelState> {

    val clipToBounds: Boolean
        get() = false

    fun overrideAnimationSpec(springSpec: SpringSpec<Float>) {
        // TODO remove default once all implementations have been migrated to BaseInterpolator
    }

    fun isAnimating(): StateFlow<Boolean> = MutableStateFlow(false)

    fun map(
        output: TransitionModel.Output<ModelState>
    ): Flow<List<FrameModel<NavTarget>>> =
        mapCore(output)

    fun mapCore(
        output: TransitionModel.Output<ModelState>
    ): Flow<List<FrameModel<NavTarget>>> =
        when (output) {
            is Keyframes -> {
                //Produce new frame model every time we switch segments
                output.currentIndexFlow.map { mapKeyframes(output, it) }
            }
            is Update -> MutableStateFlow(mapUpdate(output))
        }

    fun mapKeyframes(
        keyframes: Keyframes<ModelState>,
        segmentIndex: Int
    ): List<FrameModel<NavTarget>> =
        mapSegment(
            keyframes.currentSegment,
            keyframes.getSegmentProgress(segmentIndex),
            keyframes.progress.toSegmentProgress(segmentIndex)
                ?: throw IllegalStateException("Segment progress should be in bounds")
        )

    fun mapSegment(
        segment: Segment<ModelState>,
        segmentProgress: Flow<Float>,
        initialProgress: Float
    ): List<FrameModel<NavTarget>>

    fun mapUpdate(
        update: Update<ModelState>
    ): List<FrameModel<NavTarget>>
}
