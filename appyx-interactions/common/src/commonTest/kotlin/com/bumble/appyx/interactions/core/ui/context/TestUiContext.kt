package com.bumble.appyx.interactions.core.ui.context

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.Density
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

val TestUiContext: UiContext = UiContext(
    coroutineScope = CoroutineScope(SupervisorJob()),
    transitionBounds = TransitionBounds(
        density = object : Density {
            override val density: Float = 1f
            override val fontScale: Float = 1f
        },
        widthPx = 1080,
        heightPx = 1920,
        screenWidthPx = 1080,
        screenHeightPx = 1920,
        containerBoundsInRoot = Rect(0f, 0f, 1080f, 1920f),
    ),
    clipToBounds = true,
)
