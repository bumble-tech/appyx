package com.bumble.appyx.v2.sandbox.client.customisations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.bumble.appyx.v2.core.node.AbstractNodeView

abstract class ViewCustomisationExampleView : AbstractNodeView<ViewCustomisationExampleNode>()

class ViewCustomisationExampleDefaultView : ViewCustomisationExampleView() {

    @Composable
    override fun ViewCustomisationExampleNode.NodeView(modifier: Modifier) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Gray)
        ) {
            Text(text = "Default View")
        }
    }
}

class ViewCustomisationExampleCustomisedView : ViewCustomisationExampleView() {

    @Composable
    override fun ViewCustomisationExampleNode.NodeView(modifier: Modifier) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.LightGray)
        ) {
            Text(text = "Customised View")
        }
    }
}
