package com.bumble.appyx.v2.app.node.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import com.bumble.appyx.v2.app.composable.Page
import com.bumble.appyx.v2.app.composable.graph.Tree
import com.bumble.appyx.v2.app.composable.graph.nodeimpl.SimpleGraphNode
import com.bumble.appyx.v2.core.integration.NodeHost
import com.bumble.appyx.v2.core.integrationpoint.IntegrationPointStub
import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.node.Node

@ExperimentalUnitApi
@ExperimentalComposeUiApi
class TreeExample(
    buildContext: BuildContext,
    private val screenData: ScreenData
) : Node(
    buildContext = buildContext,
) {
    @Composable
    override fun View(modifier: Modifier) {
        Page(
            modifier = modifier,
            title = screenData.title,
            body = screenData.body
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Tree(
                    graphNode = SimpleGraphNode(
                        "Root",
                        children = listOf(
                            SimpleGraphNode(
                                label = "Onboarding",
                                children = listOf(
                                    SimpleGraphNode(label = "O1"),
                                    SimpleGraphNode(label = "O2"),
                                    SimpleGraphNode(label = "O3"),
                                )
                            ),
                            SimpleGraphNode(
                                label = "Main",
                                children = listOf(
                                    SimpleGraphNode(
                                        label = "Messages",
                                        children = listOf(
                                            SimpleGraphNode(label = "People"),
                                            SimpleGraphNode(label = "Chat"),
                                        )
                                    ),
                                    SimpleGraphNode(label = "Profile"),
                                    SimpleGraphNode(label = "Menu")
                                )
                            )
                        )
                    ),
                )
            }
        }
    }
}

@Preview
@Composable
@ExperimentalUnitApi
@ExperimentalComposeUiApi
fun TreeExamplePreview() {
    Box(Modifier.fillMaxSize()) {
        NodeHost(integrationPoint = IntegrationPointStub()) {
            TreeExample(
                BuildContext.root(null),
                screenData = ScreenData.TreeIllustration(
                    title = "Title",
                    body = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna ali- quam erat volutpat."
                )
            )
        }
    }
}
