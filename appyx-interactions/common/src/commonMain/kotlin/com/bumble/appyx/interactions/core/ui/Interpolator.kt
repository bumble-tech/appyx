package com.bumble.appyx.interactions.core.ui

import androidx.compose.animation.core.SpringSpec
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.lerp
import com.bumble.appyx.interactions.core.Keyframes
import com.bumble.appyx.interactions.core.Segment
import com.bumble.appyx.interactions.core.TransitionModel
import com.bumble.appyx.interactions.core.Update
import com.bumble.appyx.interactions.core.ui.FrameModel.State
import com.bumble.appyx.interactions.core.ui.FrameModel.State.*
import kotlinx.coroutines.flow.*

interface Interpolator<Target, ModelState> {

    fun overrideAnimationSpec(springSpec: SpringSpec<Float>) {
        // TODO remove default once all implementations have been migrated to BaseInterpolator
    }

    fun isAnimating(): StateFlow<Boolean> = MutableStateFlow(false)

    fun map(
        output: TransitionModel.Output<ModelState>
    ): Flow<List<FrameModel<Target>>> =
        applyGeometry(output)

    fun applyGeometry(
        output: TransitionModel.Output<ModelState>
    ): Flow<List<FrameModel<Target>>> =
        when (output) {
            is Keyframes -> {
                //Produce new frame model every time we switch segments
                output.currentIndexFlow.distinctUntilChanged().map { mapKeyframes(output) }
            }
            is Update -> MutableStateFlow(mapUpdate(output))
        }

    fun mapCore(
        output: TransitionModel.Output<ModelState>
    ): List<FrameModel<Target>> =
        when (output) {
            is Keyframes -> mapKeyframes(output)
            is Update -> mapUpdate(output)
        }

    fun mapKeyframes(
        keyframes: Keyframes<ModelState>
    ): List<FrameModel<Target>> =
        mapSegment(
            keyframes.currentSegment,
            keyframes.segmentProgress
        )

    fun mapSegment(
        segment: Segment<ModelState>,
        segmentProgress: StateFlow<Float>
    ): List<FrameModel<Target>>

    fun mapUpdate(
        update: Update<ModelState>
    ): List<FrameModel<Target>>


    // TODO test it
    fun resolveNavElementVisibility(
        fromProps: BaseProps,
        toProps: BaseProps,
        progress: Float
    ): State = when {
        (progress == 0.0f && !fromProps.isVisible) || (progress == 1.0f && !toProps.isVisible) -> INVISIBLE
        (progress == 0.0f && fromProps.isVisible) || (progress == 1.0f && toProps.isVisible) -> VISIBLE
        (progress > 0f && progress < 1f && (fromProps.isVisible && toProps.isVisible)) -> VISIBLE
        (progress > 0f && progress < 1f && (fromProps.isVisible || toProps.isVisible)) -> PARTIALLY_VISIBLE
        else -> INVISIBLE
    }

    // TODO extract along with other interpolation helpers
    companion object {
        fun lerpFloat(start: Float, end: Float, progress: Float): Float =
            start + progress * (end - start)

        fun lerpDpOffset(start: DpOffset, end: DpOffset, progress: Float): DpOffset =
            DpOffset(lerp(start.x, end.x, progress), lerp(start.y, end.y, progress))

        fun lerpDp(start: Dp, end: Dp, progress: Float): Dp =
            Dp(lerpFloat(start.value, end.value, progress))

    }
}
