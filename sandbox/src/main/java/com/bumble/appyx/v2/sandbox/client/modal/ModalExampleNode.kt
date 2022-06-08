package com.bumble.appyx.v2.sandbox.client.modal

import android.os.Parcelable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumble.appyx.routingsourcedemos.modal.Modal
import com.bumble.appyx.routingsourcedemos.modal.ModalTransitionHandler
import com.bumble.appyx.v2.core.composable.Children
import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.node.Node
import com.bumble.appyx.v2.core.node.ParentNode
import com.bumble.appyx.v2.sandbox.client.child.ChildNode
import com.bumble.appyx.v2.sandbox.client.modal.ModalExampleNode.Routing
import com.bumble.appyx.v2.sandbox.client.modal.ModalExampleNode.Routing.Child
import com.bumble.appyx.routingsourcedemos.modal.operation.fullScreen
import com.bumble.appyx.routingsourcedemos.modal.operation.show
import kotlinx.parcelize.Parcelize

class ModalExampleNode(
    buildContext: BuildContext,
    private val modal: Modal<Routing> = Modal(
        savedStateMap = buildContext.savedStateMap,
        initialElement = Child("first")
    )
) : ParentNode<Routing>(
    routingSource = modal,
    buildContext = buildContext,
) {

    sealed class Routing(val name: String) : Parcelable {

        abstract val value: String

        @Parcelize
        data class Child(override val value: String) : Routing(value)
    }

    override fun resolve(routing: Routing, buildContext: BuildContext): Node =
        when (routing) {
            is Child -> ChildNode("", buildContext)
        }

    @Composable
    override fun View(modifier: Modifier) {
        Text("Modal placeholder")
        Box(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
            ) {
                var fullScreenEnabled by remember { mutableStateOf(false) }
                val elements by modal.elements.collectAsState()

                Button(
                    enabled = !fullScreenEnabled,
                    onClick = {
                        fullScreenEnabled = true
                        modal.show(elements.last().key)
                    }

                ) {
                    Text("Show modal")
                }

                Button(
                    enabled = fullScreenEnabled,
                    onClick = {
                        modal.fullScreen(elements.last().key)
                    }
                ) {
                    Text("Make it fullscreen")
                }
            }

            Children(
                modifier = Modifier.fillMaxSize(),
                routingSource = modal,
                transitionHandler = ModalTransitionHandler()
            )
        }
    }

}

@Preview
@Composable
fun ModalPreview() {
    ModalExampleNode(BuildContext.root(null)).Compose()
}
