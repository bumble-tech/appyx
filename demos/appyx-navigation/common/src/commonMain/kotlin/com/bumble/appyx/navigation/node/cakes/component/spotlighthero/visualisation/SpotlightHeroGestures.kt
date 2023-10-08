package com.bumble.appyx.navigation.node.cakes.component.spotlighthero.visualisation

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.gesture.Drag
import com.bumble.appyx.interactions.core.ui.gesture.Gesture
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.interactions.core.ui.gesture.dragHorizontalDirection
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.SpotlightHeroModel
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.operation.Next
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.operation.Previous

class SpotlightHeroGestures<InteractionTarget>(
    transitionBounds: TransitionBounds,
) : GestureFactory<InteractionTarget, SpotlightHeroModel.State<InteractionTarget>> {
    private val width = transitionBounds.widthPx.toFloat()
    private val height = transitionBounds.heightPx.toFloat()

    override fun createGesture(
        state: SpotlightHeroModel.State<InteractionTarget>,
        delta: Offset,
        density: Density
    ): Gesture<InteractionTarget, SpotlightHeroModel.State<InteractionTarget>> =
        when (dragHorizontalDirection(delta)) {
            Drag.HorizontalDirection.LEFT -> Gesture(
                operation = Next(),
                completeAt = Offset(-width, 0f)
            )

            else -> Gesture(
                operation = Previous(),
                completeAt = Offset(width, 0f)
            )
        }
}
