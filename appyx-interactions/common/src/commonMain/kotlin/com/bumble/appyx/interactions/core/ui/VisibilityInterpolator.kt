package com.bumble.appyx.interactions.core.ui

import com.bumble.appyx.interactions.core.TransitionModel

interface VisibilityInterpolator<Target, ModelState> {

    fun mapVisibility(
        segment: TransitionModel.Segment<ModelState>
    ): ScreenState<Target>
}
