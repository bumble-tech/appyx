package com.bumble.appyx.sandbox.client.modal

import android.os.Parcelable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.navmodel.modal.Modal
import com.bumble.appyx.navmodel.modal.ModalTransitionHandler
import com.bumble.appyx.navmodel.modal.operation.fullScreen
import com.bumble.appyx.navmodel.modal.operation.show
import com.bumble.appyx.sandbox.client.child.ChildNode
import com.bumble.appyx.sandbox.client.modal.ModalExampleNode.NavTarget
import com.bumble.appyx.sandbox.client.modal.ModalExampleNode.NavTarget.Child
import kotlinx.parcelize.Parcelize

class ModalExampleNode(
    buildContext: BuildContext,
    private val modal: Modal<NavTarget> = Modal(
        savedStateMap = buildContext.savedStateMap,
        initialElement = Child("first")
    )
) : ParentNode<NavTarget>(
    navModel = modal,
    buildContext = buildContext,
) {

    sealed class NavTarget(val name: String) : Parcelable {

        abstract val value: String

        @Parcelize
        data class Child(override val value: String) : NavTarget(value)
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is Child -> ChildNode("", buildContext)
        }

    @Composable
    override fun View(modifier: Modifier) {
        Column(modifier.fillMaxSize()) {
            Text("Modal placeholder")
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
                navModel = modal,
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
