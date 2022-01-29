package com.bumble.appyx.v2.app.node.onboarding.screen

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
import com.bumble.appyx.v2.core.modality.BuildContext.Companion.root
import com.bumble.appyx.v2.core.node.Node

@ExperimentalUnitApi
@ExperimentalComposeUiApi
class ApplicationTree(
    buildContext: BuildContext
) : Node(
    buildContext = buildContext,
) {
    @Composable
    override fun View(modifier: Modifier) {
        Page(
            modifier = modifier,
            title = "Nodes",
            body = "The app is organised into a tree hierarchy of Nodes." +
                "\n\nNodes have @Composable UI, each have their own lifecycle on and off the screen, and can choose which of their children to delegate the control flow to."
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
fun ApplicationTreePreview() {
    Box(Modifier.fillMaxSize()) {
        NodeHost(integrationPoint = IntegrationPointStub()) {
            ApplicationTree(
                root(null),
            )
        }
    }
}
