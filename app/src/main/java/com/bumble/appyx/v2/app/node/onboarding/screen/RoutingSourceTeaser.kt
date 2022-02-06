package com.bumble.appyx.v2.app.node.onboarding.screen

import android.os.Parcelable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import com.bumble.appyx.v2.app.composable.Page
import com.bumble.appyx.v2.app.node.onboarding.screen.RoutingSourceTeaser.Routing
import com.bumble.appyx.v2.app.node.onboarding.screen.RoutingSourceTeaser.Routing.BackStackTeaser
import com.bumble.appyx.v2.app.node.onboarding.screen.RoutingSourceTeaser.Routing.RandomOtherTeaser
import com.bumble.appyx.v2.app.node.teaser.BackstackTeaserNode
import com.bumble.appyx.v2.app.node.teaser.RandomOtherTeaserNode
import com.bumble.appyx.v2.app.ui.AppyxSampleAppTheme
import com.bumble.appyx.v2.core.composable.Children
import com.bumble.appyx.v2.core.integration.NodeHost
import com.bumble.appyx.v2.core.integrationpoint.IntegrationPointStub
import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.modality.BuildContext.Companion.root
import com.bumble.appyx.v2.core.node.Node
import com.bumble.appyx.v2.core.node.ParentNode
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack
import com.bumble.appyx.v2.core.routing.source.backstack.operation.replace
import com.bumble.appyx.v2.core.routing.source.backstack.transitionhandler.rememberBackstackFader
import kotlinx.parcelize.Parcelize

@ExperimentalUnitApi
class RoutingSourceTeaser(
    buildContext: BuildContext,
    private val backStack: BackStack<Routing> = BackStack(
        initialElement = BackStackTeaser,
        savedStateMap = buildContext.savedStateMap
    ),
) : ParentNode<Routing>(
    buildContext = buildContext,
    routingSource = backStack
) {

    sealed class Routing : Parcelable {
        @Parcelize
        object BackStackTeaser : Routing()

        @Parcelize
        object RandomOtherTeaser : Routing()
    }

    override fun resolve(routing: Routing, buildContext: BuildContext): Node =
        when (routing) {
            is BackStackTeaser -> BackstackTeaserNode(buildContext)
            is RandomOtherTeaser -> RandomOtherTeaserNode(buildContext)
        }

    override fun onChildFinished(child: Node) {
        switchToNextExample()
    }

    private fun switchToNextExample() {
        val next = when (backStack.routings.value.last()) {
            is BackStackTeaser -> RandomOtherTeaser
            is RandomOtherTeaser -> BackStackTeaser
        }
        backStack.replace(next)
    }

    @Composable
    override fun View(modifier: Modifier) {
        Page(
            modifier = modifier,
            title = "Routing sources",
            body = "From simple switches to flows to back stacks to complex UI interactions, " +
                "Routing sources are a powerful concept to drive your application tree with."
        ) {
            Children(
                modifier = Modifier.fillMaxSize(),
                routingSource = backStack,
                transitionHandler = rememberBackstackFader { tween(1000) }
            )
        }
    }
}

@Preview
@Composable
@ExperimentalUnitApi
fun RoutingSourceTeaserPreview() {
    AppyxSampleAppTheme(darkTheme = false) {
        PreviewContent()
    }
}

@Preview
@Composable
@ExperimentalUnitApi
fun RoutingSourceTeaserPreviewDark() {
    AppyxSampleAppTheme(darkTheme = true) {
        PreviewContent()
    }
}

@Composable
@ExperimentalUnitApi
private fun PreviewContent() {
    Surface(color = MaterialTheme.colors.background) {
        Box(Modifier.fillMaxSize()) {
            NodeHost(integrationPoint = IntegrationPointStub()) {
                RoutingSourceTeaser(
                    root(null)
                )
            }
        }
    }
}
