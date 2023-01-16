package com.bumble.appyx.interactions.core.ui

import com.bumble.appyx.interactions.core.NavModel

interface UiProps<Target, State> {

    fun map(segment: NavModel.Segment<Target, State>): List<RenderParams<Target, State>>

    companion object {
        fun lerpFloat(start: Float, end: Float, progress: Float): Float =
            start + progress * (end - start)
    }
}
