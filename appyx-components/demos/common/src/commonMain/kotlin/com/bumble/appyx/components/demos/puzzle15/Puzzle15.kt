package com.bumble.appyx.components.demos.puzzle15

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import com.bumble.appyx.components.demos.puzzle15.Puzzle15Model.Tile
import com.bumble.appyx.components.demos.puzzle15.ui.Puzzle15MotionController
import com.bumble.appyx.interactions.core.model.BaseInteractionModel
import com.bumble.appyx.interactions.core.ui.MotionController
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class Puzzle15(
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
    model: Puzzle15Model = Puzzle15Model(savedStateMap = null),
    motionController: (UiContext) -> MotionController<Tile, Puzzle15Model.State> = {
        Puzzle15MotionController(
            it
        )
    },
    gestureFactory: (TransitionBounds) -> GestureFactory<Tile, Puzzle15Model.State> = { Puzzle15MotionController.Gestures() },
    animationSpec: AnimationSpec<Float> = spring(),
    animateSettle: Boolean = false,
) : BaseInteractionModel<Tile, Puzzle15Model.State>(
    scope = scope,
    model = model,
    motionController = motionController,
    gestureFactory = gestureFactory,
    defaultAnimationSpec = animationSpec,
    animateSettle = animateSettle
)