package com.github.zsoltk.composeribs.client.container

import android.os.Parcelable
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
import androidx.compose.ui.unit.dp
import com.github.zsoltk.composeribs.client.backstack.BackStackExampleNode
import com.github.zsoltk.composeribs.client.combined.CombinedRoutingSourceNode
import com.github.zsoltk.composeribs.client.container.ContainerNode.Routing
import com.github.zsoltk.composeribs.client.container.ContainerNode.Routing.BackStackExample
import com.github.zsoltk.composeribs.client.container.ContainerNode.Routing.CombinedRoutingSource
import com.github.zsoltk.composeribs.client.container.ContainerNode.Routing.InteractorExample
import com.github.zsoltk.composeribs.client.container.ContainerNode.Routing.LazyExamples
import com.github.zsoltk.composeribs.client.container.ContainerNode.Routing.ModalExample
import com.github.zsoltk.composeribs.client.container.ContainerNode.Routing.Picker
import com.github.zsoltk.composeribs.client.container.ContainerNode.Routing.TilesExample
import com.github.zsoltk.composeribs.client.interactorusage.InteractorNodeBuilder
import com.github.zsoltk.composeribs.client.list.LazyListContainerNode
import com.github.zsoltk.composeribs.client.modal.ModalExampleNode
import com.github.zsoltk.composeribs.client.tiles.TilesExampleNode
import com.github.zsoltk.composeribs.core.composable.Subtree
import com.github.zsoltk.composeribs.core.modality.BuildContext
import com.github.zsoltk.composeribs.core.node.Node
import com.github.zsoltk.composeribs.core.node.ParentNode
import com.github.zsoltk.composeribs.core.node.node
import com.github.zsoltk.composeribs.core.plugin.UpNavigationHandler
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.pop
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.push
import com.github.zsoltk.composeribs.core.routing.source.backstack.rememberBackstackFader
import com.github.zsoltk.composeribs.core.routing.source.backstack.rememberBackstackSlider
import com.github.zsoltk.composeribs.core.routing.transition.rememberCombinedHandler
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
        object InteractorExample : Routing()
    }

    override fun resolve(routing: Routing, buildContext: BuildContext): Node =
        when (routing) {
            is Picker -> node(buildContext) { ExamplesList() }
            is BackStackExample -> BackStackExampleNode(buildContext)
            is ModalExample -> ModalExampleNode(buildContext)
            is TilesExample -> TilesExampleNode(buildContext)
            is CombinedRoutingSource -> CombinedRoutingSourceNode(buildContext)
            is LazyExamples -> LazyListContainerNode(buildContext)
            is InteractorExample -> InteractorNodeBuilder().build(buildContext)
        }

    @Composable
    override fun View() {
        Subtree(
            modifier = Modifier.fillMaxSize(),
            routingSource = backStack,
            transitionHandler = rememberCombinedHandler(
                handlers = listOf(rememberBackstackSlider(), rememberBackstackFader())
            )
        ) {
            children<Routing> { child ->
                child()
            }
        }
    }

    @Composable
    fun ExamplesList() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TextButton("Back stack example") { backStack.push(BackStackExample) }
                TextButton("Tiles example") { backStack.push(TilesExample) }
                TextButton("Modal example") { backStack.push(ModalExample) }
                TextButton("Combined routing source") { backStack.push(CombinedRoutingSource) }
                TextButton("Node with interactor") { backStack.push(InteractorExample) }

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
    private fun TextButton(text: String, onClick: () -> Unit) {
        Button(onClick = onClick) {
            Text(text = text)
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
