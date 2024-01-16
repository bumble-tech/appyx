package com.bumble.appyx.demos.navigation.node.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bumble.appyx.demos.navigation.navigator.LocalNavigator
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.Node

class HomeNode(
    nodeContext: NodeContext,
) : Node(
    nodeContext = nodeContext,
) {

    @Composable
    override fun Content(modifier: Modifier) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val navigator = LocalNavigator.current

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Navigator demo",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.size(16.dp))
                Button(
                    onClick = {
                        navigator.goToARandomCake()
                    },
                ) {
                    Text(
                        text = "Go to a random cake",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}
