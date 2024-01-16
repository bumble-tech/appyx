package com.bumble.appyx.navigation.node

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface NodeView {

    @Composable
    fun Content(modifier: Modifier)
}
