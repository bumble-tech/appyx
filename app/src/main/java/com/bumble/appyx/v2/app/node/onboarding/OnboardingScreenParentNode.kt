package com.bumble.appyx.v2.app.node.onboarding

import android.os.Parcelable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import com.bumble.appyx.v2.app.composable.Page
import com.bumble.appyx.v2.app.node.child.GenericChildNode
import com.bumble.appyx.v2.app.node.onboarding.OnboardingScreenParentNode.Routing
import com.bumble.appyx.v2.core.integration.NodeHost
import com.bumble.appyx.v2.core.integrationpoint.IntegrationPointStub
import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.modality.BuildContext.Companion.root
import com.bumble.appyx.v2.core.node.Node
import com.bumble.appyx.v2.core.node.ParentNode
import com.bumble.appyx.v2.core.routing.source.permanent.PermanentRoutingSource
import kotlinx.parcelize.Parcelize

@ExperimentalUnitApi
class OnboardingScreenParentNode(
    buildContext: BuildContext,
    private val screenData: ScreenData.NodesExample
) : ParentNode<Routing>(
    buildContext = buildContext,
    routingSource = PermanentRoutingSource(buildContext.savedStateMap)
) {
    sealed class Routing : Parcelable {
        @Parcelize
        data class Child(val counterStartValue: Int) : Routing()
    }

    override fun resolve(routing: Routing, buildContext: BuildContext): Node =
        when (routing) {
            is Routing.Child -> GenericChildNode(buildContext, routing.counterStartValue)
        }

    @Composable
    override fun View(modifier: Modifier) {
        Page(
            modifier = modifier,
            title = screenData.title,
            body = screenData.body
        ) {
            Column(Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(bottom = 8.dp)
                ) {
                    ChildInABox(
                        routing = Routing.Child(100),
                        modifier = Modifier
                            .weight(0.5f)
                            .padding(end = 8.dp)
                    )
                    ChildInABox(
                        routing = Routing.Child(200),
                        modifier = Modifier
                            .weight(0.5f)
                    )
                }
                Row(
                    modifier = Modifier
                        .weight(0.5f)
                ) {
                    ChildInABox(
                        routing = Routing.Child(300),
                        modifier = Modifier
                            .weight(0.5f)
                            .padding(end = 8.dp)
                    )
                    ChildInABox(
                        routing = Routing.Child(400),
                        modifier = Modifier
                            .weight(0.5f)
                    )
                }
            }
        }
    }

    @Composable
    private fun ChildInABox(routing: Routing, modifier: Modifier) {
        PermanentChild(routing) { child ->
            Box(modifier) {
                AnimatedVisibility(visible = true) {
                    child()
                }
            }
        }
    }
}

@Preview
@Composable
@ExperimentalUnitApi
fun OnboardingScreenNodePreview() {
    Box(Modifier.fillMaxSize()) {
        NodeHost(integrationPoint = IntegrationPointStub()) {
            OnboardingScreenParentNode(
                root(null),
                ScreenData.NodesExample(
                    title = "Title",
                    body = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna ali- quam erat volutpat."
                )
            )
        }
    }
}
