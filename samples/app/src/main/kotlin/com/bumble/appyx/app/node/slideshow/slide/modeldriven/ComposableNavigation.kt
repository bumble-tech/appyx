package com.bumble.appyx.app.node.slideshow.slide.modeldriven

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
import com.bumble.appyx.app.composable.Page
import com.bumble.appyx.app.composable.graph.GraphNode
import com.bumble.appyx.app.composable.graph.Tree
import com.bumble.appyx.app.composable.graph.nodeimpl.SimpleGraphNode
import com.bumble.appyx.app.ui.AppyxSampleAppTheme
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
    Surface(color = MaterialTheme.colors.background) {
        Box(Modifier.fillMaxSize()) {
            NodeHost(integrationPoint = IntegrationPointStub()) {
                ComposableNavigation(
                    root(null),
                )
            }
        }
    }
}
