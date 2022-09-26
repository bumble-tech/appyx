package com.bumble.appyx.core.node

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier

@Stable
interface NodeView {

    @Composable
    fun View(modifier: Modifier)
}
