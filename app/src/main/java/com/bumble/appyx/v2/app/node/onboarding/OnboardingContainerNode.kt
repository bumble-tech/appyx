package com.bumble.appyx.v2.app.node.onboarding

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumble.appyx.v2.app.node.onboarding.OnboardingContainerNode.Routing
import com.bumble.appyx.v2.core.composable.Children
import com.bumble.appyx.v2.core.integration.NodeHost
import com.bumble.appyx.v2.core.integrationpoint.IntegrationPointStub
import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.node.Node
import com.bumble.appyx.v2.core.node.ParentNode
import com.bumble.appyx.v2.core.node.node
import com.bumble.appyx.v2.core.routing.source.spotlight.Spotlight
import com.bumble.appyx.v2.core.routing.source.spotlight.hasNext
import com.bumble.appyx.v2.core.routing.source.spotlight.hasPrevious
import com.bumble.appyx.v2.core.routing.source.spotlight.operations.next
import com.bumble.appyx.v2.core.routing.source.spotlight.operations.previous
import com.bumble.appyx.v2.core.routing.source.spotlight.transitionhandlers.rememberSpotlightSlider
import kotlinx.parcelize.Parcelize

class OnboardingContainerNode(
    buildContext: BuildContext,
    private val spotlight: Spotlight<Routing, Routing> = Spotlight(
        items = getItems(),
        savedStateMap = buildContext.savedStateMap,
    )
) : ParentNode<Routing>(
    routingSource = spotlight,
    buildContext = buildContext
) {
    sealed class Routing : Parcelable {
        @Parcelize
        object Splash : Routing()

        @Parcelize
        data class OnboardingScreen(
            val title: String
        ) : Routing()
    }

    companion object {
        private fun getItems() = listOf(
            Routing.Splash,
            Routing.OnboardingScreen("Hello"),
            Routing.OnboardingScreen("Second screen"),
        )
    }

    override fun resolve(routing: Routing, buildContext: BuildContext): Node =
        when (routing) {
            is Routing.Splash -> screenNode(buildContext) { Text(text = "Splash") }
            is Routing.OnboardingScreen -> screenNode(buildContext) { Text(text = routing.title) }
        }

    private fun screenNode(buildContext: BuildContext, content: @Composable () -> Unit): Node =
        node(buildContext) { modifier -> Screen(modifier, content) }

    @Composable
    private fun Screen(modifier: Modifier, content: @Composable () -> Unit) {
        Box(
            modifier = modifier.fillMaxSize()
        ) {
            content()
        }
    }

    @Composable
    override fun View(modifier: Modifier) {
        val hasPrevious = spotlight.hasPrevious().collectAsState(initial = false)
        val hasNext = spotlight.hasNext().collectAsState(initial = false)

        Column(
            modifier = modifier
                .background(Color.DarkGray)
                .fillMaxSize()
        ) {
            Children(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 12.dp)
                    .background(Color.Cyan),
                transitionHandler = rememberSpotlightSlider(clipToBounds = true),
                routingSource = spotlight
            ) {
                children<Routing> { child ->
                    child()
                }
            }

            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    text = "Previous",
                    enabled = hasPrevious.value
                ) {
                    spotlight.previous()
                }
                TextButton(
                    text = "Next",
                    enabled = hasNext.value
                ) {
                    spotlight.next()
                }
            }
        }
    }

    // TODO extract or use Material TextButton instead
    @Composable
    private fun TextButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
        Button(onClick = onClick, enabled = enabled, modifier = Modifier.padding(4.dp)) {
            Text(text = text)
        }
    }
}

@Preview
@Composable
fun GenericChildNodePreview() {
    Box(Modifier.size(200.dp)) {
        NodeHost(integrationPoint = IntegrationPointStub()) {
            OnboardingContainerNode(BuildContext.root(null))
        }
    }
}
