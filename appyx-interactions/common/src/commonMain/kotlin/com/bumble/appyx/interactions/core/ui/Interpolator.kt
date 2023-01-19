package com.bumble.appyx.interactions.core.ui

import com.bumble.appyx.interactions.core.TransitionModel

interface Interpolator<Target, ModelState> {

    fun map(
        segment: TransitionModel.Segment<ModelState>
    ): List<FrameModel<Target>>

    // TODO extract along with other interpolation helpers
    companion object {
        fun lerpFloat(start: Float, end: Float, progress: Float): Float =
            start + progress * (end - start)
    }
}
