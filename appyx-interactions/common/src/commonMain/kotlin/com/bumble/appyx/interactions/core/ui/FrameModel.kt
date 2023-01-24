package com.bumble.appyx.interactions.core.ui

import com.bumble.appyx.interactions.core.NavElement
import androidx.compose.ui.Modifier

data class FrameModel<NavTarget>(
    val navElement: NavElement<NavTarget>,
    val modifier: Modifier,
    val progress: Float,
)
