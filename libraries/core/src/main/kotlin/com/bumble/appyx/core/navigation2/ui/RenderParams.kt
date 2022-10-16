package com.bumble.appyx.core.navigation2.ui

import androidx.compose.ui.Modifier
import com.bumble.appyx.core.navigation2.NavElement

class RenderParams<Target, State>(
    val navElement: NavElement<Target, State>,
    val modifier: Modifier
)
