package com.bumble.appyx.appyxnavigation.node.slideshow.slide.app

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import com.bumble.appyx.appyxnavigation.composable.Page
import com.bumble.appyx.appyxnavigation.composable.dashedBorder
import com.bumble.appyx.appyxnavigation.ui.AppyxSampleAppTheme
import com.bumble.appyx.core.integration.NodeHost
import com.bumble.appyx.core.integrationpoint.IntegrationPointStub
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.modality.BuildContext.Companion.root
import com.bumble.appyx.core.node.Node
import kotlinx.coroutines.delay

@ExperimentalUnitApi
class StatefulNode2(
    buildContext: BuildContext,
) : Node(
    buildContext = buildContext
) {

    @Composable
    override fun View(modifier: Modifier) {
        Page(
            modifier = modifier,
            title = "Nodes are alive",
            body = "– even when they're not visible. " +
                    "\n\nTry going back to the previous screen! " +
                    "You should see that the counters kept on working in the background, " +
                    "and changes you made to colours are persisted."
        ) {
            val shape = RoundedCornerShape(16.dp)

            Column(
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.Center)
                    .background(
                        color = if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray,
                        shape = shape
                    )
                    .dashedBorder(
                        width = 4.dp,
                        color = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray,
                        shape = shape,
                        on = 8.dp,
                        off = 6.dp
                    )
                    .padding(16.dp)

            ) {
                var counter by remember { mutableStateOf(105) }
                LaunchedEffect(Unit) {
                    while (true) {
                        delay(1000)
                        counter++
                    }
                }

                Text(
                    text = "Child (3456)",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "id = 3456",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "counter = $counter",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "color = #03A9F4",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview
@Composable
@ExperimentalUnitApi
fun StatefulNode2Preview() {
    AppyxSampleAppTheme(darkTheme = false) {
        PreviewContent()
    }
}

@Preview
@Composable
@ExperimentalUnitApi
fun StatefulNode2PreviewDark() {
    AppyxSampleAppTheme(darkTheme = true) {
        PreviewContent()
    }
}

@Composable
@ExperimentalUnitApi
private fun PreviewContent() {
    Surface(color = MaterialTheme.colorScheme.background) {
        Box(Modifier.fillMaxSize()) {
            NodeHost(integrationPoint = IntegrationPointStub()) {
                StatefulNode2(
                    root(null)
                )
            }
        }
    }
}
