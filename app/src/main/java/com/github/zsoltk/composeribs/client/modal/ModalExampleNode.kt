package com.github.zsoltk.composeribs.client.modal

import android.os.Parcelable
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.github.zsoltk.composeribs.client.child.ChildNode
import com.github.zsoltk.composeribs.client.modal.ModalExampleNode.Routing
import com.github.zsoltk.composeribs.client.modal.ModalExampleNode.Routing.Child
import com.github.zsoltk.composeribs.core.Node
import com.github.zsoltk.composeribs.core.ParentNode
import com.github.zsoltk.composeribs.core.modality.BuildContext
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack
import kotlinx.parcelize.Parcelize

class ModalExampleNode(
    buildContext: BuildContext,
) : ParentNode<Routing>(
    routingSource = BackStack(Child, buildContext.savedStateMap),
    buildContext = buildContext,
) {

    sealed class Routing : Parcelable {
        @Parcelize
        object Child : Routing()
    }

    override fun resolve(routing: Routing, buildContext: BuildContext): Node =
        when (routing) {
            is Child -> ChildNode("", buildContext)
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
    ModalExampleNode(BuildContext.root(null)).Compose()
}
