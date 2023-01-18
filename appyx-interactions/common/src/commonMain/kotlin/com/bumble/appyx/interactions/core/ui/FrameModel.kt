package com.bumble.appyx.interactions.core.ui

import com.bumble.appyx.interactions.core.NavElement
import androidx.compose.ui.Modifier

data class FrameModel<Target, NavState>(
    val navElement: NavElement<Target, NavState>,
    val modifier: Modifier,
    val progress: Float,
)
