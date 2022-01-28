package com.bumble.appyx.v2.app.node.onboarding

import android.os.Parcelable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import com.bumble.appyx.v2.app.composable.Page
import com.bumble.appyx.v2.app.node.child.GenericChildNode
import com.bumble.appyx.v2.app.node.onboarding.StatefulNodeExample.Routing
import com.bumble.appyx.v2.core.integration.NodeHost
import com.bumble.appyx.v2.core.integrationpoint.IntegrationPointStub
import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.modality.BuildContext.Companion.root
import com.bumble.appyx.v2.core.node.Node
import com.bumble.appyx.v2.core.node.ParentNode
import com.bumble.appyx.v2.core.routing.source.permanent.PermanentRoutingSource
import kotlinx.coroutines.delay
import kotlinx.parcelize.Parcelize

@ExperimentalUnitApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
class StatefulNodeExample(
    buildContext: BuildContext,
    private val screenData: ScreenData
) : ParentNode<Routing>(
    buildContext = buildContext,
    routingSource = PermanentRoutingSource(
        key = "permanent_key",
        savedStateMap = buildContext.savedStateMap)
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
                        routing = Routing.Child(BASE_COUNTER),
                        showWithDelay = BASE_DELAY,
                        modifier = Modifier
                            .weight(0.5f)
                            .padding(end = 8.dp)
                    )
                    ChildInABox(
                        routing = Routing.Child(BASE_COUNTER * 2),
                        showWithDelay = BASE_DELAY * 2,
                        modifier = Modifier
                            .weight(0.5f)
                    )
                }
                Row(
                    modifier = Modifier
                        .weight(0.5f)
                ) {
                    ChildInABox(
                        routing = Routing.Child(BASE_COUNTER * 3),
                        showWithDelay = BASE_DELAY * 3,
                        modifier = Modifier
                            .weight(0.5f)
                            .padding(end = 8.dp)
                    )
                    ChildInABox(
                        routing = Routing.Child(BASE_COUNTER * 4),
                        showWithDelay = BASE_DELAY * 4,
                        modifier = Modifier
                            .weight(0.5f)
                    )
                }
            }
        }
    }

    @Composable
    private fun ChildInABox(routing: Routing, showWithDelay: Long, modifier: Modifier) {
        PermanentChild(routing) { child ->
            Box(modifier) {
                var visible by remember { mutableStateOf(false) }
                AnimatedVisibility(
                    visible = visible,
                    enter = scaleIn()
                ) {
                    child()
                }

                LaunchedEffect(Unit) {
                    delay(showWithDelay)
                    visible = true
                }
            }
        }
    }

    companion object {
        private const val BASE_COUNTER = 100
        private const val BASE_DELAY = 200L
    }
}

@Preview
@Composable
@ExperimentalUnitApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
fun OnboardingScreenNodePreview() {
    Box(Modifier.fillMaxSize()) {
        NodeHost(integrationPoint = IntegrationPointStub()) {
            StatefulNodeExample(
                root(null),
                ScreenData.StatefulNodeIllustration(
                   title = "Title",
                    body = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna ali- quam erat volutpat."
                )
            )
        }
    }
}
