package com.bumble.appyx.interactions.core.ui

import com.bumble.appyx.interactions.core.TransitionModel

interface UiProps<Target, State> {

    fun map(segment: TransitionModel.Segment<Target, State>): List<FrameModel<Target, State>>

    companion object {
        fun lerpFloat(start: Float, end: Float, progress: Float): Float =
            start + progress * (end - start)
    }
}
