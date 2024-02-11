package com.bumble.appyx.demos.navigation.component.spotlighthero.visualisation

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import com.bumble.appyx.demos.navigation.component.spotlighthero.SpotlightHeroModel
import com.bumble.appyx.demos.navigation.component.spotlighthero.SpotlightHeroModel.Mode.HERO
import com.bumble.appyx.demos.navigation.component.spotlighthero.SpotlightHeroModel.Mode.LIST
import com.bumble.appyx.demos.navigation.component.spotlighthero.operation.Next
import com.bumble.appyx.demos.navigation.component.spotlighthero.operation.Previous
import com.bumble.appyx.demos.navigation.component.spotlighthero.operation.SetHeroMode
import com.bumble.appyx.interactions.ui.context.TransitionBounds
import com.bumble.appyx.interactions.gesture.Drag
import com.bumble.appyx.interactions.gesture.Gesture
import com.bumble.appyx.interactions.gesture.GestureFactory
import com.bumble.appyx.interactions.gesture.dragDirection4

class SpotlightHeroGestures<NavTarget>(
    transitionBounds: TransitionBounds,
) : GestureFactory<NavTarget, SpotlightHeroModel.State<NavTarget>> {
    private val width = transitionBounds.widthPx.toFloat()
    private val height = transitionBounds.heightPx.toFloat()

    override fun createGesture(
        state: SpotlightHeroModel.State<NavTarget>,
        delta: Offset,
        density: Density
    ): Gesture<NavTarget, SpotlightHeroModel.State<NavTarget>> =
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
