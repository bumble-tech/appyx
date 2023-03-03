package com.bumble.appyx.navigation.node.slideshow.slide.modeldriven

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import com.bumble.appyx.navigation.composable.Page
import com.bumble.appyx.navigation.composable.graph.GraphNode
import com.bumble.appyx.navigation.composable.graph.Tree
import com.bumble.appyx.navigation.composable.graph.nodeimpl.SimpleGraphNode
import com.bumble.appyx.navigation.ui.AppyxSampleAppTheme
import com.bumble.appyx.core.integration.NodeHost
import com.bumble.appyx.core.integrationpoint.IntegrationPointStub
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.modality.BuildContext.Companion.root
import com.bumble.appyx.core.node.Node
import kotlinx.coroutines.delay

@ExperimentalUnitApi
@ExperimentalComposeUiApi
class ComposableNavigation(
    buildContext: BuildContext
) : Node(
    buildContext = buildContext,
) {
    private val o1 = SimpleGraphNode(label = "O1")
    private val o2 = SimpleGraphNode(label = "O2")
    private val o3 = SimpleGraphNode(label = "O3")
    private val onboarding = SimpleGraphNode(
        label = "Onboarding",
        children = listOf(
            o1,
            o2,
            o3,
        )
    )

    private val people = SimpleGraphNode(label = "People")
    private val chat = SimpleGraphNode(label = "Chat")
    private val messages = SimpleGraphNode(
        label = "Messages",
        children = listOf(
            people,
            chat,
        )
    )

    private val settings = SimpleGraphNode(label = "Settings")
    private val profile = SimpleGraphNode(label = "Profile")
    private val main = SimpleGraphNode(
        label = "Main",
        children = listOf(
            messages,
            profile,
            settings
        )
    )
    private val root: GraphNode = SimpleGraphNode(
        label = "Root",
        children = listOf(
            onboarding,
            main,
        )
    )

    private fun deselect(node: GraphNode) {
        node.isActive.value = false
        for (child in node.children()) {
            deselect(child)
        }
    }

    private fun deselectAllNodes() {
        deselect(root)
    }

    private fun selectNode(
        currentNode: GraphNode,
        targetNode: GraphNode,
        pathToNode: List<GraphNode>
    ) {
        if (currentNode == targetNode) {
            currentNode.isActive.value = true
            pathToNode.forEach {
                it.isActive.value = true
            }
            return
        } else {
            for (child in currentNode.children()) {
                selectNode(child, targetNode, pathToNode + child)
            }
        }

    }

    private fun selectNode(nodeToSelect: GraphNode) {
        deselectAllNodes()
        selectNode(root, nodeToSelect, emptyList())
    }

    @SuppressWarnings("LongMethod")
    @Composable
    override fun View(modifier: Modifier) {
        Page(
            modifier = modifier,
            title = "Composable",
            body = "With Appyx, navigation itself is composable, too.\n" +
                    "\n" +
                    "You can represent your app as a hierarchy of Nodes – " +
                    "each with their own UI, lifecycle and their own NavModels."
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Tree(graphNode = root)
            }
        }

        LaunchedEffect(Unit) {
            val startDelay: Long = 500
            val intervalDelay: Long = 1600

            while (true) {

                delay(startDelay)
                selectNode(o1)

                delay(intervalDelay)
                selectNode(o2)

                delay(intervalDelay)
                selectNode(o3)

                delay(intervalDelay)
                selectNode(profile)

                delay(intervalDelay)
                selectNode(people)

                delay(intervalDelay)
                selectNode(chat)

                delay(intervalDelay)
                selectNode(settings)

                delay(intervalDelay * 2)
            }
        }
    }
}

@Preview
@Composable
@ExperimentalUnitApi
@ExperimentalComposeUiApi
fun ComposableNavigationPreview() {
    AppyxSampleAppTheme(darkTheme = false) {
        PreviewContent()
    }
}

@Preview
@Composable
@ExperimentalUnitApi
@ExperimentalComposeUiApi
fun ComposableNavigationPreviewDark() {
    AppyxSampleAppTheme(darkTheme = true) {
        PreviewContent()
    }
}

@Composable
@ExperimentalUnitApi
@ExperimentalComposeUiApi
private fun PreviewContent() {
    Surface(color = MaterialTheme.colorScheme.background) {
        Box(Modifier.fillMaxSize()) {
            NodeHost(integrationPoint = IntegrationPointStub()) {
                ComposableNavigation(
                    root(null),
                )
            }
        }
    }
}
