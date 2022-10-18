package com.bumble.appyx.core.navigation2.ui

import com.bumble.appyx.core.navigation2.NavModel

interface UiProps<Target, State> {

    fun map(segment: NavModel.Segment<Target, State>): List<RenderParams<Target, State>>

    companion object {
        fun lerpFloat(start: Float, end: Float, progress: Float): Float =
            start + progress * (end - start)
    }
}
