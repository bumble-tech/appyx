package com.bumble.appyx.components.experimental.puzzle15

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import com.bumble.appyx.components.experimental.puzzle15.Puzzle15Model.Tile
import com.bumble.appyx.components.experimental.puzzle15.ui.Puzzle15Visualisation
import com.bumble.appyx.interactions.core.model.BaseAppyxComponent
import com.bumble.appyx.interactions.core.ui.Visualisation
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class Puzzle15(
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
    model: Puzzle15Model = Puzzle15Model(savedStateMap = null),
    visualisation: (UiContext) -> Visualisation<Tile, Puzzle15Model.State> = {
        Puzzle15Visualisation(it)
    },
    gestureFactory: (TransitionBounds) -> GestureFactory<Tile, Puzzle15Model.State> = { bounds ->
        Puzzle15Visualisation.Gestures(bounds)
    },
    animationSpec: AnimationSpec<Float> = spring(),
    animateSettle: Boolean = false,
) : BaseAppyxComponent<Tile, Puzzle15Model.State>(
    scope = scope,
    model = model,
    visualisation = visualisation,
    gestureFactory = gestureFactory,
    defaultAnimationSpec = animationSpec,
    animateSettle = animateSettle
)
