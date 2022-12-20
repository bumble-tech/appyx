package com.bumble.appyx.sandbox.client.customisations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.bumble.appyx.core.node.NodeView

class ViewCustomisationExampleDefaultView : NodeView {

    @Composable
    override fun View(modifier: Modifier) {
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

class ViewCustomisationExampleCustomisedView : NodeView {

    @Composable
    override fun View(modifier: Modifier) {
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
