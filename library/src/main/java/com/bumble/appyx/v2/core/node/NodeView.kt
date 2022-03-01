package com.bumble.appyx.v2.core.node

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface NodeView {
    @Composable
    fun View(modifier: Modifier)
}
