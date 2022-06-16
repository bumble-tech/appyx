package com.bumble.appyx.app.composable.graph.nodeimpl

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import com.bumble.appyx.app.composable.graph.GraphNode
import com.bumble.appyx.app.node.onboarding.screen.ApplicationTree
import com.bumble.appyx.app.ui.md_amber_300
import com.bumble.appyx.app.ui.md_blue_300
import com.bumble.appyx.app.ui.md_blue_grey_300
import com.bumble.appyx.app.ui.md_cyan_300
import com.bumble.appyx.app.ui.md_grey_300
import com.bumble.appyx.app.ui.md_indigo_300
import com.bumble.appyx.app.ui.md_light_blue_300
import com.bumble.appyx.app.ui.md_light_green_300
import com.bumble.appyx.app.ui.md_lime_300
import com.bumble.appyx.app.ui.md_pink_300
import com.bumble.appyx.app.ui.md_teal_300
import com.bumble.appyx.core.integration.NodeHost
import com.bumble.appyx.core.integrationpoint.IntegrationPointStub
import com.bumble.appyx.core.modality.BuildContext.Companion.root
import kotlin.random.Random

private val colors = listOf(
    md_pink_300,
    md_indigo_300,
    md_blue_300,
    md_light_blue_300,
    md_cyan_300,
    md_teal_300,
    md_light_green_300,
    md_lime_300,
    md_amber_300,
    md_grey_300,
    md_blue_grey_300
)

class SimpleGraphNode(
    private val label: String,
    private val color: Color = colors[Random.nextInt(colors.size)],
    private val children: List<GraphNode> = emptyList()
) : GraphNode {

    override val isActive: MutableState<Boolean> =
        mutableStateOf(false)

    override fun children(): List<GraphNode> =
        children

    @Composable
    override fun View(modifier: Modifier) {
        val cornerRadius = 8.dp
        val horizontalPadding = 4.dp
        val internalPadding = 6.dp
        val alpha by animateFloatAsState(targetValue = if (isActive.value) 1f else 0.25f)

        Box(
            modifier = modifier
                .wrapContentSize()
                .padding(start = horizontalPadding, end = horizontalPadding)
                .background(
                    color = MaterialTheme.colors.surface,
                    shape = RoundedCornerShape(cornerRadius)
                )
                .alpha(alpha)
                .background(
                    color = color,
                    shape = RoundedCornerShape(cornerRadius)
                )
                .padding(internalPadding)
        ) {
            Text(
                text = label,
                modifier = Modifier.align(Alignment.Center),
                color = Color.Black,
                style = MaterialTheme.typography.caption
            )
        }
    }
}

@Preview
@Composable
@ExperimentalUnitApi
@ExperimentalComposeUiApi
fun SimpleGraphNodePreview() {
    Box(Modifier.fillMaxSize()) {
        NodeHost(integrationPoint = IntegrationPointStub()) {
            ApplicationTree(
                buildContext = root(null),
            )
        }
    }
}
