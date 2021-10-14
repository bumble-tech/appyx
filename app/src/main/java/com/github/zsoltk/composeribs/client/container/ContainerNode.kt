package com.github.zsoltk.composeribs.client.container

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.github.zsoltk.composeribs.core.node
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState.CREATED
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState.DESTROYED
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState.ON_SCREEN
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState.STASHED_IN_BACK_STACK
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackFader
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackSlider
import com.github.zsoltk.composeribs.core.routing.transition.CombinedHandler
import com.github.zsoltk.composeribs.core.routing.transition.UpdateTransitionHandler

class ContainerNode(
    private val backStack: BackStack<Routing> = BackStack(initialElement = Picker),
    private val transitionHandler: UpdateTransitionHandler<BackStack.TransitionState> = CombinedHandler(
        listOf(
            BackStackSlider(transitionSpec = { tween(1000) }),
            BackStackFader(transitionSpec = { tween(500, easing = LinearEasing) }),
        )
    )
) : Node<Routing>(
    routingSource = backStack,
) {

    sealed class Routing {
        object Picker : Routing()
        object BackStackExample : Routing()
        object TilesExample : Routing()
        object ModalExample : Routing()
    }

    override fun resolve(routing: Routing): Node<*> =
        when (routing) {
            is Picker -> node { ExamplesList() }
            is BackStackExample -> BackStackExampleNode()
            is ModalExample -> ModalExampleNode()
            is TilesExample -> TilesExampleNode()
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
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // TODO variant 1
            Subtree(backStack, transitionHandler) {
                children<Routing> { transitionModifier, child ->
                    val background = transition.animateColor(label = "color") { state ->
                        when (state) {
                            CREATED -> Color.Yellow
                            ON_SCREEN -> Color.Red
                            STASHED_IN_BACK_STACK -> Color.Blue
                            DESTROYED -> Color.Black
                        }

                    }
                    Box(modifier = transitionModifier.background(background.value)) {
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
            }
        }
    }
}
