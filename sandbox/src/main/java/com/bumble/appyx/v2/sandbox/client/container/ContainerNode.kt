package com.bumble.appyx.v2.sandbox.client.container

import android.content.Intent
import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bumble.appyx.utils.customisations.NodeCustomisation
import com.bumble.appyx.v2.core.composable.Children
import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.node.Node
import com.bumble.appyx.v2.core.node.ParentNode
import com.bumble.appyx.v2.core.node.node
import com.bumble.appyx.v2.core.plugin.UpNavigationHandler
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack
import com.bumble.appyx.v2.core.routing.source.backstack.operation.pop
import com.bumble.appyx.v2.core.routing.source.backstack.operation.push
import com.bumble.appyx.v2.core.routing.source.backstack.transitionhandler.rememberBackstackFader
import com.bumble.appyx.v2.core.routing.source.backstack.transitionhandler.rememberBackstackSlider
import com.bumble.appyx.v2.core.routing.transition.rememberCombinedHandler
import com.bumble.appyx.v2.sandbox.client.backstack.BackStackExampleNode
import com.bumble.appyx.v2.sandbox.client.combined.CombinedRoutingSourceNode
import com.bumble.appyx.v2.sandbox.client.container.ContainerNode.Routing
import com.bumble.appyx.v2.sandbox.client.container.ContainerNode.Routing.BackStackExample
import com.bumble.appyx.v2.sandbox.client.container.ContainerNode.Routing.CombinedRoutingSource
import com.bumble.appyx.v2.sandbox.client.container.ContainerNode.Routing.InteractorExample
import com.bumble.appyx.v2.sandbox.client.container.ContainerNode.Routing.LazyExamples
import com.bumble.appyx.v2.sandbox.client.container.ContainerNode.Routing.ModalExample
import com.bumble.appyx.v2.sandbox.client.container.ContainerNode.Routing.MviCoreExample
import com.bumble.appyx.v2.sandbox.client.container.ContainerNode.Routing.Picker
import com.bumble.appyx.v2.sandbox.client.container.ContainerNode.Routing.RequestPermissionsExamples
import com.bumble.appyx.v2.sandbox.client.container.ContainerNode.Routing.RoutingSourcesExamples
import com.bumble.appyx.v2.sandbox.client.container.ContainerNode.Routing.SpotlightExample
import com.bumble.appyx.v2.sandbox.client.container.ContainerNode.Routing.TilesExample
import com.bumble.appyx.v2.sandbox.client.integrationpoint.IntegrationPointExampleNode
import com.bumble.appyx.v2.sandbox.client.interactorusage.InteractorNodeBuilder
import com.bumble.appyx.v2.sandbox.client.interop.InteropExampleActivity
import com.bumble.appyx.v2.sandbox.client.interop.parent.V1ParentRib
import com.bumble.appyx.v2.sandbox.client.interop.parent.V1ParentViewImpl.Factory
import com.bumble.appyx.v2.sandbox.client.list.LazyListContainerNode
import com.bumble.appyx.v2.sandbox.client.modal.ModalExampleNode
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.MviCoreExampleBuilder
import com.bumble.appyx.v2.sandbox.client.spotlight.SpotlightExampleNode
import com.bumble.appyx.v2.sandbox.client.tiles.TilesExampleNode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

class ContainerNode(
    buildContext: BuildContext,
    private val backStack: BackStack<Routing> = BackStack(
        initialElement = Picker,
        savedStateMap = buildContext.savedStateMap,
    )
) : ParentNode<Routing>(
    routingSource = backStack,
    buildContext = buildContext,
), UpNavigationHandler {

    class Customisation(val name: String? = null) : NodeCustomisation

    private val label: String? = buildContext.getOrDefault(Customisation()).name

    private val upNavigationOverridesChild: MutableStateFlow<Boolean> = MutableStateFlow(true)

    sealed class Routing : Parcelable {
        @Parcelize
        object Picker : Routing()

        @Parcelize
        object BackStackExample : Routing()

        @Parcelize
        object TilesExample : Routing()

        @Parcelize
        object ModalExample : Routing()

        @Parcelize
        object CombinedRoutingSource : Routing()

        @Parcelize
        object LazyExamples : Routing()

        @Parcelize
        object RequestPermissionsExamples : Routing()

        @Parcelize
        object RoutingSourcesExamples : Routing()

        @Parcelize
        object SpotlightExample : Routing()

        @Parcelize
        object InteractorExample : Routing()

        @Parcelize
        object MviCoreExample : Routing()
    }

    override fun resolve(routing: Routing, buildContext: BuildContext): Node =
        when (routing) {
            is Picker -> node(buildContext) { modifier -> ExamplesList(modifier) }
            is RoutingSourcesExamples -> node(buildContext) { modifier -> RoutingSources(modifier) }
            is BackStackExample -> BackStackExampleNode(buildContext)
            is ModalExample -> ModalExampleNode(buildContext)
            is TilesExample -> TilesExampleNode(buildContext)
            is CombinedRoutingSource -> CombinedRoutingSourceNode(buildContext)
            is LazyExamples -> LazyListContainerNode(buildContext)
            is SpotlightExample -> SpotlightExampleNode(buildContext)
            is InteractorExample -> InteractorNodeBuilder().build(buildContext)
            is RequestPermissionsExamples -> IntegrationPointExampleNode(buildContext)
            is MviCoreExample -> MviCoreExampleBuilder().build(
                buildContext,
                "MVICore initial state"
            )
        }

    @Composable
    override fun View(modifier: Modifier) {
        Children(
            modifier = modifier.fillMaxSize(),
            routingSource = backStack,
            transitionHandler = rememberCombinedHandler(
                handlers = listOf(rememberBackstackSlider(), rememberBackstackFader())
            )
        ) {
            children<Routing> { child, descriptor ->
                val color = when (descriptor.element) {
                    is BackStackExample -> Color.LightGray
                    else -> Color.White
                }
                child(modifier = Modifier.background(color))
            }
        }
    }

    @Composable
    fun ExamplesList(modifier: Modifier) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                label?.let {
                    Text(it, textAlign = TextAlign.Center)
                }
                TextButton("MVICore Example") { backStack.push(MviCoreExample) }
                TextButton("Launch interop example") {
                    integrationPoint.activityStarter.startActivity {
                        Intent(this, InteropExampleActivity::class.java)
                    }
                }
                TextButton("Routing Sources Examples") { backStack.push(RoutingSourcesExamples) }

                val scope = rememberCoroutineScope()
                TextButton("Trigger double navigation in 3 seconds") {
                    scope.launch {
                        delay(3_000)
                        backStack.push(BackStackExample)
                        backStack.push(TilesExample)
                    }
                }
                TextButton("LazyList") { backStack.push(LazyExamples) }
                Row {
                    Checkbox(
                        checked = upNavigationOverridesChild.collectAsState().value,
                        onCheckedChange = { upNavigationOverridesChild.value = it }
                    )
                    Text(text = "Up navigation overrides child")
                }
            }
        }
    }

    @Composable
    fun RoutingSources(modifier: Modifier) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TextButton("Backstack example") { backStack.push(BackStackExample) }
                TextButton("Tiles example") { backStack.push(TilesExample) }
                TextButton("Modal example") { backStack.push(ModalExample) }
                TextButton("Combined routing source") { backStack.push(CombinedRoutingSource) }
                TextButton("Node with interactor") { backStack.push(Routing.InteractorExample) }
                TextButton("Spotlight Example") { backStack.push(SpotlightExample) }
                TextButton("Request permissions / start activities example") {
                    backStack.push(RequestPermissionsExamples)
                }
            }
        }
    }

    @Composable
    private fun TextButton(text: String, onClick: () -> Unit) {
        Button(onClick = onClick) {
            Text(textAlign = TextAlign.Center, text = text)
        }
    }

    override fun handleUpNavigation(): Boolean =
        if (upNavigationOverridesChild.value && backStack.canHandleBackPress.value) {
            backStack.pop()
            true
        } else {
            false
        }
}
