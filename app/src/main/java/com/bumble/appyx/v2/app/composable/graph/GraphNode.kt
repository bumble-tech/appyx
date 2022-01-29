package com.bumble.appyx.v2.app.composable.graph

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface GraphNode {

    fun children(): List<GraphNode>

    @Composable
    fun View(modifier: Modifier)
}
