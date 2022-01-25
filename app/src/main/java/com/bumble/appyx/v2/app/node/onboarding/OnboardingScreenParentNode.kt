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
        object Child : Routing()
    }

    override fun resolve(routing: Routing, buildContext: BuildContext): Node =
        when (routing) {
            Routing.Child -> GenericChildNode(buildContext)
        }

    @Composable
    override fun View(modifier: Modifier) {
        Page(
            modifier = modifier,
            title = screenData.title,
            body = screenData.body
        ) {
            PermanentChild(Routing.Child) { child ->
                Box(
                    modifier = Modifier
                        .size(200.dp)
                ) {
                    AnimatedVisibility(visible = true) {
                        child()
                    }
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
