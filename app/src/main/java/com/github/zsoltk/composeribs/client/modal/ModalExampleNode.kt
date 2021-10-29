package com.github.zsoltk.composeribs.client.modal

import android.os.Parcelable
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.github.zsoltk.composeribs.client.child.ChildNode
import com.github.zsoltk.composeribs.client.modal.ModalExampleNode.Routing
import com.github.zsoltk.composeribs.client.modal.ModalExampleNode.Routing.Child
import com.github.zsoltk.composeribs.core.Node
import com.github.zsoltk.composeribs.core.SavedStateMap
import kotlinx.parcelize.Parcelize

class ModalExampleNode(
    savedStateMap: SavedStateMap?,
) : Node<Routing>(
    routingSource = null,
    savedStateMap = savedStateMap,
) {

    sealed class Routing : Parcelable {
        @Parcelize
        object Child : Routing()
    }

    override fun resolve(routing: Routing, savedStateMap: SavedStateMap?): Node<*> =
        when (routing) {
            is Child -> ChildNode("", savedStateMap)
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
    ModalExampleNode(null).Compose()
}
