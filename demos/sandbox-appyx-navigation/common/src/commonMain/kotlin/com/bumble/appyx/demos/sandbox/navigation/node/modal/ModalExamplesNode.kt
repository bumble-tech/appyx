package com.bumble.appyx.demos.sandbox.navigation.node.modal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.modal.Modal
import com.bumble.appyx.components.modal.ModalModel
import com.bumble.appyx.components.modal.operation.add
import com.bumble.appyx.components.modal.operation.fullScreen
import com.bumble.appyx.components.modal.operation.revert
import com.bumble.appyx.components.modal.operation.show
import com.bumble.appyx.components.modal.ui.ModalVisualisation
import com.bumble.appyx.demos.sandbox.navigation.colors
import com.bumble.appyx.demos.sandbox.navigation.node.modal.ModalExamplesNode.NavTarget
import com.bumble.appyx.demos.sandbox.navigation.ui.TextButton
import com.bumble.appyx.demos.sandbox.navigation.ui.appyx_dark
import com.bumble.appyx.navigation.composable.AppyxNavigationContainer
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.AbstractNode
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.node
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import kotlin.random.Random


class ModalExamplesNode(
    nodeContext: NodeContext,
    private val modal: Modal<NavTarget> = Modal(
        model = ModalModel(
            initialElements = listOf(NavTarget.Child),
            savedStateMap = nodeContext.savedStateMap
        ),
        visualisation = { ModalVisualisation(it) }
    )
) : ParentNode<NavTarget>(
    nodeContext = nodeContext,
    appyxComponent = modal
) {

    sealed class NavTarget : Parcelable {
        @Parcelize
        object Child : NavTarget()
    }

    override fun buildChildNode(navTarget: NavTarget, nodeContext: NodeContext): AbstractNode =
        when (navTarget) {
            is NavTarget.Child -> node(nodeContext) {
                val backgroundColor = remember { colors.shuffled().random() }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundColor)
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = Random.nextInt(0, 100).toString(),
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

    @Composable
    override fun Content(modifier: Modifier) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(appyx_dark),
            verticalArrangement = Arrangement.Bottom
        ) {
            AppyxNavigationContainer(
                appyxComponent = modal,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.9f)
            )
            Row(
                modifier = Modifier
                    .weight(0.1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    text = "Add",
                    modifier = Modifier
                        .padding(horizontal = 4.dp),
                ) {
                    modal.add(NavTarget.Child)
                }
                TextButton(
                    text = "Show",
                    modifier = Modifier
                        .padding(horizontal = 4.dp),
                ) {
                    modal.show()
                }
                TextButton(
                    text = "Full",
                    modifier = Modifier
                        .padding(horizontal = 4.dp),
                ) {
                    modal.fullScreen()
                }
                TextButton(
                    text = "Revert",
                    modifier = Modifier
                        .padding(horizontal = 4.dp),
                ) {
                    modal.revert()
                }
            }
        }
    }
}

