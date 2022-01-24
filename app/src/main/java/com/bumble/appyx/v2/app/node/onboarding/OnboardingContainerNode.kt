package com.bumble.appyx.v2.app.node.onboarding

import android.os.Parcelable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumble.appyx.v2.app.node.onboarding.OnboardingContainerNode.*
import com.bumble.appyx.v2.connectable.rx2.Connectable
import com.bumble.appyx.v2.connectable.rx2.NodeConnector
import com.bumble.appyx.v2.core.composable.Children
import com.bumble.appyx.v2.core.integration.NodeHost
import com.bumble.appyx.v2.core.integrationpoint.IntegrationPointStub
import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.node.Node
import com.bumble.appyx.v2.core.node.ParentNode
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
        data class OnboardingScreen(
            val screenData: ScreenData
        ) : Routing()
    }

    companion object {
        private fun getItems() = listOf(
            Routing.OnboardingScreen(onboardingScreenWelcome),
            Routing.OnboardingScreen(onboardingScreenNodes),
            Routing.OnboardingScreen(onboardingScreenLifecycle1),
            Routing.OnboardingScreen(onboardingScreenLifecycle2),
        )
    }

    override fun resolve(routing: Routing, buildContext: BuildContext): Node =
        when (routing) {
            is Routing.OnboardingScreen -> OnboardingScreenNode(buildContext, routing.screenData)
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
                if (hasNext.value) {
                    TextButton(
                        onClick = { spotlight.next() }
                    ) {
                        Text(
                            text = "Next".toUpperCase(Locale.current),
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    TextButton(
                        onClick = { output.accept(Output.FinishedOnboarding) }
                    ) {
                        Text(
                            text = "Done".toUpperCase(Locale.current),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun OnboardingContainerNodePreview() {
    Box(Modifier.fillMaxSize()) {
        NodeHost(integrationPoint = IntegrationPointStub()) {
            OnboardingContainerNode(BuildContext.root(null))
        }
    }
}
