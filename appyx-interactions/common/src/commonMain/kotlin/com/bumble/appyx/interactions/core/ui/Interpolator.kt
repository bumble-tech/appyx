package com.bumble.appyx.interactions.core.ui

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.lerp
import com.bumble.appyx.interactions.core.TransitionModel
import com.bumble.appyx.interactions.core.TransitionModel.Output.Segment
import com.bumble.appyx.interactions.core.TransitionModel.Output.Update
import com.bumble.appyx.interactions.core.ui.FrameModel.State
import com.bumble.appyx.interactions.core.ui.FrameModel.State.INVISIBLE
import com.bumble.appyx.interactions.core.ui.FrameModel.State.PARTIALLY_VISIBLE
import com.bumble.appyx.interactions.core.ui.FrameModel.State.VISIBLE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface Interpolator<Target, ModelState> {

    fun isAnimating(): StateFlow<Boolean> = MutableStateFlow(false)

    fun map(
        output: TransitionModel.Output<ModelState>
    ): StateFlow<List<FrameModel<Target>>> =
        applyGeometry(output)

    fun applyGeometry(
        output: TransitionModel.Output<ModelState>
    ): StateFlow<List<FrameModel<Target>>> =
        MutableStateFlow(mapCore(output))

    fun mapCore(
        output: TransitionModel.Output<ModelState>
    ): List<FrameModel<Target>> =
        when (output) {
            is Segment -> mapSegment(output)
            is Update -> mapUpdate(output)
        }


    fun mapSegment(
        segment: Segment<ModelState>
    ): List<FrameModel<Target>>

    fun mapUpdate(
        update: Update<ModelState>
    ): List<FrameModel<Target>>


    // TODO test it
    fun resolveNavElementVisibility(
        fromProps: BaseProps,
        toProps: BaseProps,
        progress: Float = 1f
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
