package com.bumble.appyx.interactions.core.ui

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.lerp
import com.bumble.appyx.interactions.core.TransitionModel
import com.bumble.appyx.interactions.core.ui.FrameModel.State.INVISIBLE
import com.bumble.appyx.interactions.core.ui.FrameModel.State.PARTIALLY_VISIBLE
import com.bumble.appyx.interactions.core.ui.FrameModel.State.VISIBLE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface Interpolator<Target, ModelState> {

    fun map(
        segment: TransitionModel.Segment<ModelState>
    ): StateFlow<List<FrameModel<Target>>> =
        MutableStateFlow(mapFrame(segment))

    fun mapFrame(
        segment: TransitionModel.Segment<ModelState>
    ): List<FrameModel<Target>>

    // TODO extract along with other interpolation helpers
    companion object {
        fun lerpFloat(start: Float, end: Float, progress: Float): Float =
            start + progress * (end - start)

        fun lerpDpOffset(start: DpOffset, end: DpOffset, progress: Float): DpOffset =
            DpOffset(lerp(start.x, end.x, progress), lerp(start.y, end.y, progress))

        fun lerpDp(start: Dp, end: Dp, progress: Float): Dp =
            Dp(lerpFloat(start.value, end.value, progress))

        fun resolveNavElementVisibility(
            fromProps: BaseProps,
            toProps: BaseProps
        ): FrameModel.State {
            return if (fromProps.isVisible && toProps.isVisible) {
                VISIBLE
            } else if (!fromProps.isVisible && !toProps.isVisible) {
                INVISIBLE
            } else {
                PARTIALLY_VISIBLE
            }
        }
    }
}
