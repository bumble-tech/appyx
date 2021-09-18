package com.github.zsoltk.composeribs.client.container.modal

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.zsoltk.composeribs.client.child.ChildNode
import com.github.zsoltk.composeribs.client.container.modal.ModalExampleNode.Routing
import com.github.zsoltk.composeribs.client.container.modal.ModalExampleNode.Routing.Child
import com.github.zsoltk.composeribs.core.Node
import com.github.zsoltk.composeribs.core.routing.SubtreeController
import com.github.zsoltk.composeribs.core.routing.source.modal.Modal
import com.github.zsoltk.composeribs.core.routing.source.modal.ModalElement
import com.github.zsoltk.composeribs.core.routing.source.modal.ModalTransitionHandler
import kotlin.random.Random

class ModalExampleNode(
    private val modal: Modal<Routing> = Modal(
        initialElements = listOf(Child(0))
    )
) : Node<Routing>(
    subtreeController = SubtreeController(
        routingSource = modal,
        transitionHandler = ModalTransitionHandler(
            transitionSpec = { tween(1500) }
        )
    )
) {

    sealed class Routing {
        data class Child(val counter: Int) : Routing()
    }

    override fun resolve(routing: Routing): Node<*> =
        when (routing) {
            is Child -> ChildNode(routing.counter)
        }

    @Composable
    override fun View() {
        Box(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
            ) {
                var fullScreenEnabled by remember { mutableStateOf(false) }
                var lastKey by remember { mutableStateOf<ModalElement<Routing>?>(null) }

                Button(
                    enabled = !fullScreenEnabled,
                    onClick = {
                        fullScreenEnabled = true
                        lastKey = modal.add(Child(Random.nextInt(9999))).also {
                            modal.showModal(it.key) }
                        }

                ) {
                    Text("Show modal")
                }

                Button(
                    enabled = fullScreenEnabled,
                    onClick = {
                        lastKey?.let {
                            modal.fullScreen(it.key)
                        }
                    }
                ) {
                    Text("Make it fullscreen")
                }
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                placeholder<Routing>()
            }
        }
    }
}

@Preview
@Composable
fun ModalPreview() {
    ModalExampleNode().Compose()
}

