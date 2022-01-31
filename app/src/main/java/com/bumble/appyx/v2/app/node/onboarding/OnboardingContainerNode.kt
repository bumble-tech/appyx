package com.bumble.appyx.v2.app.node.onboarding

import android.os.Parcelable
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import com.bumble.appyx.v2.app.node.onboarding.OnboardingContainerNode.Input
import com.bumble.appyx.v2.app.node.onboarding.OnboardingContainerNode.Output
import com.bumble.appyx.v2.app.node.onboarding.OnboardingContainerNode.Routing
import com.bumble.appyx.v2.app.node.onboarding.screen.ApplicationTree
import com.bumble.appyx.v2.app.node.onboarding.screen.IntroScreen
import com.bumble.appyx.v2.app.node.onboarding.screen.RoutingSourceTeaser
import com.bumble.appyx.v2.app.node.onboarding.screen.StatefulNode1
import com.bumble.appyx.v2.app.node.onboarding.screen.StatefulNode2
import com.bumble.appyx.v2.app.ui.AppyxSampleAppTheme
import com.bumble.appyx.v2.app.ui.appyx_dark
import com.bumble.appyx.v2.connectable.rx2.Connectable
import com.bumble.appyx.v2.connectable.rx2.NodeConnector
import com.bumble.appyx.v2.core.composable.Children
import com.bumble.appyx.v2.core.integration.NodeHost
import com.bumble.appyx.v2.core.integrationpoint.IntegrationPointStub
import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.modality.BuildContext.Companion.root
import com.bumble.appyx.v2.core.node.Node
import com.bumble.appyx.v2.core.node.ParentNode
import com.bumble.appyx.v2.core.routing.source.spotlight.Spotlight
import com.bumble.appyx.v2.core.routing.source.spotlight.hasNext
import com.bumble.appyx.v2.core.routing.source.spotlight.hasPrevious
import com.bumble.appyx.v2.core.routing.source.spotlight.operations.next
import com.bumble.appyx.v2.core.routing.source.spotlight.operations.previous
import com.bumble.appyx.v2.core.routing.source.spotlight.transitionhandlers.rememberSpotlightSlider
import kotlinx.parcelize.Parcelize

@ExperimentalUnitApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
class OnboardingContainerNode(
    buildContext: BuildContext,
    private val spotlight: Spotlight<Routing, Routing> = Spotlight(
        items = listOf(
            Routing.IntroScreen,
            Routing.ApplicationTree,
            Routing.StatefulNode1,
            Routing.StatefulNode2,
            Routing.RoutingSource,
        ),
        savedStateMap = buildContext.savedStateMap,
    ),
    connectable: Connectable<Input, Output> = NodeConnector()
) : ParentNode<Routing>(
    routingSource = spotlight,
    buildContext = buildContext
), Connectable<Input, Output> by connectable {

    sealed class Input

    sealed class Output {
        object FinishedOnboarding : Output()
    }

    sealed class Routing : Parcelable {
        @Parcelize
        object IntroScreen : Routing()

        @Parcelize
        object ApplicationTree : Routing()

        @Parcelize
        object StatefulNode1 : Routing()

        @Parcelize
        object StatefulNode2 : Routing()

        @Parcelize
        object RoutingSource : Routing()
    }

    override fun resolve(routing: Routing, buildContext: BuildContext): Node =
        when (routing) {
            Routing.IntroScreen -> IntroScreen(buildContext)
            Routing.ApplicationTree -> ApplicationTree(buildContext)
            Routing.StatefulNode1 -> StatefulNode1(buildContext)
            Routing.StatefulNode2 -> StatefulNode2(buildContext)
            Routing.RoutingSource -> RoutingSourceTeaser(buildContext)
        }

    @Composable
    override fun View(modifier: Modifier) {
        val hasPrevious = spotlight.hasPrevious().collectAsState(initial = false)
        val hasNext = spotlight.hasNext().collectAsState(initial = false)

        Column(
            modifier = modifier
                .fillMaxSize()
        ) {
            Children(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                transitionHandler = rememberSpotlightSlider(clipToBounds = true),
                routingSource = spotlight
            ) {
                children<Routing> { child ->
                    child()
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (hasNext.value) {
                    if (hasPrevious.value) {
                        TextButton(
                            onClick = { spotlight.previous() }
                        ) {
                            Text(
                                text = "Previous".toUpperCase(Locale.current),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Spacer(Modifier)
                    }
                    TextButton(
                        onClick = { spotlight.next() }
                    ) {
                        Text(
                            text = "Next".toUpperCase(Locale.current),
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Spacer(Modifier)
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { output.accept(Output.FinishedOnboarding) }
                    ) {
                        Text(
                            text = "Check it out!",
                            color = appyx_dark,
                        )
                    }
                    Spacer(Modifier)
                }
            }
        }
    }
}

@Preview
@Composable
@ExperimentalUnitApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
fun OnboardingContainerNodePreview() {
    AppyxSampleAppTheme(darkTheme = false) {
        PreviewContent()
    }
}

@Preview
@Composable
@ExperimentalUnitApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
fun OnboardingContainerNodePreviewDark() {
    AppyxSampleAppTheme(darkTheme = true) {
        PreviewContent()
    }
}

@Composable
@ExperimentalUnitApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
private fun PreviewContent() {
    Surface(color = MaterialTheme.colors.background) {
        Box(Modifier.fillMaxSize()) {
            NodeHost(integrationPoint = IntegrationPointStub()) {
                OnboardingContainerNode(root(null))
            }
        }
    }
}

