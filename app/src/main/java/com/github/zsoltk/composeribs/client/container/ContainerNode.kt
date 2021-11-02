package com.github.zsoltk.composeribs.client.container

import android.os.Parcelable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.github.zsoltk.composeribs.client.container.ContainerNode.Routing
import com.github.zsoltk.composeribs.client.container.ContainerNode.Routing.BackStackExample
import com.github.zsoltk.composeribs.client.container.ContainerNode.Routing.ModalExample
import com.github.zsoltk.composeribs.client.container.ContainerNode.Routing.Picker
import com.github.zsoltk.composeribs.client.container.ContainerNode.Routing.TilesExample
import com.github.zsoltk.composeribs.client.modal.ModalExampleNode
import com.github.zsoltk.composeribs.client.tiles.TilesExampleNode
import com.github.zsoltk.composeribs.core.Node
import com.github.zsoltk.composeribs.core.Subtree
import com.github.zsoltk.composeribs.core.modality.BuildContext
import com.github.zsoltk.composeribs.core.node
import com.github.zsoltk.composeribs.core.plugin.UpNavigationHandler
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackFader
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackSlider
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.pop
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.push
import com.github.zsoltk.composeribs.core.routing.transition.CombinedHandler
import com.github.zsoltk.composeribs.core.routing.transition.UpdateTransitionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

class ContainerNode(
    buildContext: BuildContext,
    private val backStack: BackStack<Routing> = BackStack(
        initialElement = Picker,
        savedStateMap = buildContext.savedStateMap,
    ),
    private val transitionHandler: UpdateTransitionHandler<TransitionState> = CombinedHandler(
        listOf(
            BackStackSlider(transitionSpec = { tween(1000) }),
            BackStackFader(transitionSpec = { tween(500, easing = LinearEasing) }),
        )
    )
) : Node<Routing>(
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
    }

    override fun resolve(routing: Routing, buildContext: BuildContext): Node<*> =
        when (routing) {
            is Picker -> node(buildContext) { ExamplesList() }
            is BackStackExample -> BackStackExampleNode(buildContext)
            is ModalExample -> ModalExampleNode(buildContext)
            is TilesExample -> TilesExampleNode(buildContext)
        }

//    @OptIn(ExperimentalAnimationApi::class)
//    @Composable
//    override fun View(foo: StateObject) {
//        Box(modifier = Modifier.fillMaxSize()) {
//            Subtree(backStack) {
//                children<Routing> { child, routingElement ->
//                    // TODO you can also use routingElement.fromState / targetState for e.g. updateTransition
//                    AnimatedVisibility(
//                        visible = routingElement.onScreen,
//                        enter = fadeIn(animationSpec = tween(1000)),
//                        exit = fadeOut(animationSpec = tween(1000)),
//                    ) {
//                        child()
//                    }
//                }
//            }
//        }
//    }

    @Composable
    override fun View() {
        // TODO variant 1
        Subtree(
            modifier = Modifier.fillMaxSize(),
            routingSource = backStack,
            transitionHandler = transitionHandler
        ) {
            children<Routing> { transitionModifier, child ->
                Box(modifier = transitionModifier) {
                    child()
                }
            }
        }

        // TODO variant 2, decide which one is better
//            SubtreeVariant(backStack, transitionHandler) { transitionModifier, child ->
//                Box(modifier = transitionModifier) {
//                    child()
//                }
//            }
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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = { backStack.push(Routing.BackStackExample) }) {
                    Text(text = "Back stack example")
                }
                Spacer(modifier = Modifier.size(24.dp))
                Button(onClick = { backStack.push(Routing.TilesExample) }) {
                    Text(text = "Tiles example")
                }
                Spacer(modifier = Modifier.size(24.dp))
                Button(onClick = { backStack.push(Routing.ModalExample) }) {
                    Text(text = "Modal example")
                }
                Spacer(modifier = Modifier.size(24.dp))
                val scope = rememberCoroutineScope()
                Button(onClick = {
                    scope.launch {
                        delay(3_000)
                        backStack.push(Routing.BackStackExample)
                        backStack.push(Routing.TilesExample)
                    }
                }) {
                    Text(text = "Trigger double navigation in 3 seconds")
                }
                Spacer(modifier = Modifier.size(24.dp))
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

    override fun handleUpNavigation(): Boolean =
        if (upNavigationOverridesChild.value && backStack.canHandleBackPress.value) {
            backStack.pop()
            true
        } else {
            false
        }
}
