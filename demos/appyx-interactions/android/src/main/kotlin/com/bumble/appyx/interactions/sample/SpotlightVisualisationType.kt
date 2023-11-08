package com.bumble.appyx.interactions.sample

import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.ui.sliderrotation.SpotlightSliderRotation
import com.bumble.appyx.interactions.core.ui.context.UiContext

enum class SpotlightVisualisationType {
    SLIDER_ROTATION;

    fun toVisualisation(
        uiContext: UiContext,
        state: SpotlightModel.State<InteractionTarget>,
    ) = when (this) {
        SLIDER_ROTATION -> SpotlightSliderRotation(uiContext, state)
    }
}
