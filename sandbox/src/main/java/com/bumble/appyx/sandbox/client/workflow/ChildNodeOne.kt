package com.bumble.appyx.sandbox.client.workflow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node

class ChildNodeOne(buildContext: BuildContext) : Node(buildContext) {

    @Composable
    override fun View(modifier: Modifier) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(color = Color.LightGray)
        ) {
            Text(
                text = "Child one",
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
