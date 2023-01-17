package com.bumble.appyx.interactions.core.ui

import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.core.NavElement

class FrameModel<Target, State>(
    val navElement: NavElement<Target, State>,
    val modifier: Modifier
)
