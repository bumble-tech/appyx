package com.bumble.appyx.v2.app.node.onboarding.screen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import com.bumble.appyx.v2.app.composable.Page
import com.bumble.appyx.v2.app.composable.graph.GraphNode
import com.bumble.appyx.v2.app.composable.graph.Tree
import com.bumble.appyx.v2.app.composable.graph.nodeimpl.SimpleGraphNode
import com.bumble.appyx.v2.app.ui.AppyxSampleAppTheme
import com.bumble.appyx.v2.core.integration.NodeHost
import com.bumble.appyx.v2.core.integrationpoint.IntegrationPointStub
import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.modality.BuildContext.Companion.root
import com.bumble.appyx.v2.core.node.Node
import kotlinx.coroutines.delay

@ExperimentalUnitApi
@ExperimentalComposeUiApi
class ApplicationTree(
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

    @Composable
    override fun View(modifier: Modifier) {
        Page(
            modifier = modifier,
            title = "Nodes",
            body = "The app is organised into a tree of Nodes." +
                "\n\nNodes have @Composable UI, each have their own lifecycle on and off the screen, and can choose which of their children to delegate the control flow to."
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
            val intervalDelay: Long = 1200

            while (true) {
                root.isActive.value = false
                onboarding.isActive.value = false
                o1.isActive.value = false
                o2.isActive.value = false
                o3.isActive.value = false
                main.isActive.value = false
                settings.isActive.value = false
                profile.isActive.value = false
                messages.isActive.value = false
                people.isActive.value = false
                chat.isActive.value = false

                delay(startDelay)
                root.isActive.value = true
                onboarding.isActive.value = true
                o1.isActive.value = true
                delay(intervalDelay)
                o1.isActive.value = false
                o2.isActive.value = true
                delay(intervalDelay)
                o2.isActive.value = false
                o3.isActive.value = true

                delay(intervalDelay)
                o3.isActive.value = false
                onboarding.isActive.value = false
                main.isActive.value = true
                profile.isActive.value = true

                delay(intervalDelay)
                profile.isActive.value = false
                messages.isActive.value = true
                people.isActive.value = true

                delay(intervalDelay)
                people.isActive.value = false
                chat.isActive.value = true

                delay(intervalDelay)
                messages.isActive.value = false
                chat.isActive.value = false
                settings.isActive.value = true

                delay(intervalDelay * 2)
            }
        }
    }
}

@Preview
@Composable
@ExperimentalUnitApi
@ExperimentalComposeUiApi
fun ApplicationTreePreview() {
    AppyxSampleAppTheme(darkTheme = false) {
        PreviewContent()
    }
}

@Preview
@Composable
@ExperimentalUnitApi
@ExperimentalComposeUiApi
fun ApplicationTreePreviewDark() {
    AppyxSampleAppTheme(darkTheme = true) {
        PreviewContent()
    }
}

@Composable
@ExperimentalUnitApi
@ExperimentalComposeUiApi
private fun PreviewContent() {
    Surface(color = MaterialTheme.colors.background) {
        Box(Modifier.fillMaxSize()) {
            NodeHost(integrationPoint = IntegrationPointStub()) {
                ApplicationTree(
                    root(null),
                )
            }
        }
    }
}
