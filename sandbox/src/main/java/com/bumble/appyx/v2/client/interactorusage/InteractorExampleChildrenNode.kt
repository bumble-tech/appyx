package com.bumble.appyx.v2.client.interactorusage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.node.Node

class Child2Node(buildContext: BuildContext) : Node(buildContext) {

    @Composable
    override fun View(modifier: Modifier) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(color = Color.LightGray)
        )
    }
}

class Child3Node(buildContext: BuildContext) : Node(buildContext) {

    @Composable
    override fun View(modifier: Modifier) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(color = Color.DarkGray)
        )
    }
}
