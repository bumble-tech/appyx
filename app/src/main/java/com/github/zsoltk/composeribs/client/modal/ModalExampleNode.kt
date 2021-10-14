package com.github.zsoltk.composeribs.client.modal

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.github.zsoltk.composeribs.client.child.ChildNode
import com.github.zsoltk.composeribs.client.modal.ModalExampleNode.Routing
import com.github.zsoltk.composeribs.client.modal.ModalExampleNode.Routing.Child
import com.github.zsoltk.composeribs.core.Node
//import com.github.zsoltk.composeribs.core.routing.source.modal.Modal
//import com.github.zsoltk.composeribs.core.routing.source.modal.ModalElement
//import com.github.zsoltk.composeribs.core.routing.source.modal.ModalTransitionHandler

class ModalExampleNode(
//    private val modal: Modal<Routing> = Modal(
//        initialElements = listOf(Child(0))
//    )
) : Node<Routing>(
//    subtreeController = SubtreeController(
        routingSource = null // modal,
//        transitionHandler = ModalTransitionHandler(
//            transitionSpec = { tween(1500) }
//        )
//    )
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
        Text("Modal placeholder")
//        Box(Modifier.fillMaxSize()) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(24.dp),
//            ) {
//                var fullScreenEnabled by remember { mutableStateOf(false) }
//                var lastKey by remember { mutableStateOf<ModalElement<Routing>?>(null) }
//
//                Button(
//                    enabled = !fullScreenEnabled,
//                    onClick = {
//                        fullScreenEnabled = true
//                        lastKey = modal.add(Child(Random.nextInt(9999))).also {
//                            modal.showModal(it.key) }
//                        }
//
//                ) {
//                    Text("Show modal")
//                }
//
//                Button(
//                    enabled = fullScreenEnabled,
//                    onClick = {
//                        lastKey?.let {
//                            modal.fullScreen(it.key)
//                        }
//                    }
//                ) {
//                    Text("Make it fullscreen")
//                }
//            }
//
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                placeholder<Routing>()
//            }
//        }
    }
}

@Preview
@Composable
fun ModalPreview() {
    ModalExampleNode().Compose()
}
