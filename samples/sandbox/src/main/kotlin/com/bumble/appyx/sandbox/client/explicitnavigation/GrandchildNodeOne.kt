package com.bumble.appyx.sandbox.client.explicitnavigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.sandbox.client.explicitnavigation.treenavigator.Navigator

class GrandchildNodeOne(
    buildContext: BuildContext,
    private val navigator: Navigator
) : Node(buildContext) {

    @Composable
    override fun View(modifier: Modifier) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color = Color.LightGray)
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.Center)
            ) {
                Text(text = "Grandchild one", modifier = Modifier.align(CenterHorizontally))
                Spacer(modifier = Modifier.requiredHeight(8.dp))
                Button(onClick = { navigator.navigateToChildOne() }) {
                    Text(text = "Navigate to child one")
                }
            }
        }
    }
}
