package com.bumble.appyx.navigation.composable.graph

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier

interface GraphNode {

    val isActive: MutableState<Boolean>

    fun children(): List<GraphNode>

    @Composable
    fun View(modifier: Modifier)
}
