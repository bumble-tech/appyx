package com.github.zsoltk.composeribs.client.container

import androidx.compose.animation.core.tween
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
import androidx.compose.ui.unit.dp
import com.github.zsoltk.composeribs.client.backstack.BackStackExampleNode
import com.github.zsoltk.composeribs.client.container.ContainerNode.Routing
import com.github.zsoltk.composeribs.client.container.ContainerNode.Routing.*
import com.github.zsoltk.composeribs.client.container.ContainerNode.Routing.Picker
import com.github.zsoltk.composeribs.client.modal.ModalExampleNode
import com.github.zsoltk.composeribs.client.tiles.TilesExampleNode
import com.github.zsoltk.composeribs.core.Node
import com.github.zsoltk.composeribs.core.node
import com.github.zsoltk.composeribs.core.routing.SubtreeController
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackSlider

class ContainerNode(
    private val backStack: BackStack<Routing> = BackStack(initialElement = Picker)
) : Node<Routing>(
    subtreeController = SubtreeController(
        routingSource = backStack,
        transitionHandler = BackStackSlider(
            transitionSpec = { tween(1000) }
        )
    )
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

    @Composable
    override fun View() {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            placeholder<Routing>()
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
