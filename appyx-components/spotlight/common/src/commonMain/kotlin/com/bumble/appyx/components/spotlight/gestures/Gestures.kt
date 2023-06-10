package com.bumble.appyx.components.spotlight.gestures

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import com.bumble.appyx.components.spotlight.SpotlightModel.State
import com.bumble.appyx.components.spotlight.operation.Next
import com.bumble.appyx.components.spotlight.operation.Previous
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.gesture.Drag
import com.bumble.appyx.interactions.core.ui.gesture.Gesture
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.interactions.core.ui.gesture.dragHorizontalDirection
import com.bumble.appyx.interactions.core.ui.gesture.dragVerticalDirection

class Gestures<InteractionTarget>(
    transitionBounds: TransitionBounds,
    private val orientation: Orientation = Orientation.Horizontal,
    private val reverseOrientation: Boolean = false,
) : GestureFactory<InteractionTarget, State<InteractionTarget>> {
    private val width = transitionBounds.widthPx.toFloat()
    private val height = transitionBounds.heightPx.toFloat()

    override fun createGesture(
        state: State<InteractionTarget>,
        delta: Offset,
        density: Density
    ): Gesture<InteractionTarget, State<InteractionTarget>> = when (orientation) {
        Orientation.Horizontal -> {
            when (dragHorizontalDirection(delta)) {
                Drag.HorizontalDirection.LEFT -> Gesture(
                    operation = if (reverseOrientation) Previous() else Next(),
                    completeAt = Offset(-width, 0f)
                )

                else -> Gesture(
                    operation = if (reverseOrientation) Next() else Previous(),
                    completeAt = Offset(width, 0f)
                )
            }
        }

        Orientation.Vertical -> {
            when (dragVerticalDirection(delta)) {
                Drag.VerticalDirection.UP -> Gesture(
                    operation = if (reverseOrientation) Previous() else Next(),
                    completeAt = Offset(0f, -height)
                )

                else ->
                    Gesture(
                        operation = if (reverseOrientation) Next() else Previous(),
                        completeAt = Offset(0f, height)
                    )
            }
        }
    }
}
