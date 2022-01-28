package com.bumble.appyx.v2.app.composable.graph.nodeimpl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import com.bumble.appyx.v2.app.composable.graph.GraphNode
import com.bumble.appyx.v2.app.node.onboarding.ScreenData
import com.bumble.appyx.v2.app.node.onboarding.TreeExample
import com.bumble.appyx.v2.app.ui.atomic_tangerine
import com.bumble.appyx.v2.app.ui.manatee
import com.bumble.appyx.v2.app.ui.silver_sand
import com.bumble.appyx.v2.app.ui.sizzling_red
import com.bumble.appyx.v2.core.integration.NodeHost
import com.bumble.appyx.v2.core.integrationpoint.IntegrationPointStub
import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.sandbox.ui.md_amber_500
import com.bumble.appyx.v2.sandbox.ui.md_blue_500
import com.bumble.appyx.v2.sandbox.ui.md_blue_grey_500
import com.bumble.appyx.v2.sandbox.ui.md_cyan_500
import com.bumble.appyx.v2.sandbox.ui.md_grey_500
import com.bumble.appyx.v2.sandbox.ui.md_indigo_500
import com.bumble.appyx.v2.sandbox.ui.md_light_blue_500
import com.bumble.appyx.v2.sandbox.ui.md_light_green_500
import com.bumble.appyx.v2.sandbox.ui.md_lime_500
import com.bumble.appyx.v2.sandbox.ui.md_pink_500
import com.bumble.appyx.v2.sandbox.ui.md_teal_500
import kotlin.random.Random

private val colors = listOf(
    manatee,
    sizzling_red,
    atomic_tangerine,
    silver_sand,
    md_pink_500,
    md_indigo_500,
    md_blue_500,
    md_light_blue_500,
    md_cyan_500,
    md_teal_500,
    md_light_green_500,
    md_lime_500,
    md_amber_500,
    md_grey_500,
    md_blue_grey_500
)

class SimpleGraphNode(
    private val label: String,
    private val color: Color = colors[Random.nextInt(colors.size)],
    private val children: List<GraphNode> = emptyList()
) : GraphNode {

    override fun children(): List<GraphNode> =
        children

    @Composable
    override fun View(modifier: Modifier) {
        Box(
            modifier = modifier
                .wrapContentSize()
                .padding(start = 4.dp, end = 4.dp)
                .background(
                    color = color,
                    shape = RoundedCornerShape(6.dp)
                )
                .padding(4.dp)
        ) {
            Text(
                text = label,
                modifier = Modifier.align(Alignment.Center),
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
            TreeExample(
                BuildContext.root(null),
                screenData = ScreenData.TreeIllustration(
                    texts = ScreenData.Texts(
                        title = "Title",
                        body = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna ali- quam erat volutpat."
                    )
                )
            )
        }
    }
}
