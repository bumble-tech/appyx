package com.bumble.appyx.navigation.component.spotlighthero.visualisation

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.gesture.Drag
import com.bumble.appyx.interactions.core.ui.gesture.Gesture
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.interactions.core.ui.gesture.dragDirection4
import com.bumble.appyx.navigation.component.spotlighthero.SpotlightHeroModel
import com.bumble.appyx.navigation.component.spotlighthero.SpotlightHeroModel.Mode.HERO
import com.bumble.appyx.navigation.component.spotlighthero.SpotlightHeroModel.Mode.LIST
import com.bumble.appyx.navigation.component.spotlighthero.operation.Next
import com.bumble.appyx.navigation.component.spotlighthero.operation.Previous
import com.bumble.appyx.navigation.component.spotlighthero.operation.SetHeroMode

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
        when (dragDirection4(delta)) {
            Drag.Direction4.LEFT -> Gesture(
                operation = Next(),
                completeAt = Offset(-width, 0f)
            )

            Drag.Direction4.RIGHT -> Gesture(
                operation = Previous(),
                completeAt = Offset(width, 0f)
            )

            Drag.Direction4.UP -> Gesture(
                operation = SetHeroMode(HERO),
                completeAt = Offset(0f, -height / 4)
            )

            Drag.Direction4.DOWN -> Gesture(
                operation = SetHeroMode(LIST),
                completeAt = Offset(0f, height / 4)
            )

            else -> Gesture.Noop()
        }
}
