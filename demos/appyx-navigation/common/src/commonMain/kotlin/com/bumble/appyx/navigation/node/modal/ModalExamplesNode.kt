package com.bumble.appyx.navigation.node.modal

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
import com.bumble.appyx.components.modal.ui.ModalMotionController
import com.bumble.appyx.navigation.colors
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.modal.ModalExamplesNode.InteractionTarget
import com.bumble.appyx.navigation.node.node
import com.bumble.appyx.navigation.ui.TextButton
import com.bumble.appyx.navigation.ui.appyx_dark
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import kotlin.random.Random


class ModalExamplesNode(
    buildContext: BuildContext,
    private val modal: Modal<InteractionTarget> = Modal(
        model = ModalModel(
            initialElements = listOf(InteractionTarget.Child),
            savedStateMap = buildContext.savedStateMap
        ),
        motionController = { ModalMotionController(it) }
    )
) : ParentNode<InteractionTarget>(
    buildContext = buildContext,
    appyxComponent = modal
) {

    sealed class InteractionTarget : Parcelable {
        @Parcelize
        object Child : InteractionTarget()
    }

    override fun resolve(interactionTarget: InteractionTarget, buildContext: BuildContext): Node =
        when (interactionTarget) {
            is InteractionTarget.Child -> node(buildContext) {
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
    override fun View(modifier: Modifier) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(appyx_dark),
            verticalArrangement = Arrangement.Bottom
        ) {
            AppyxComponent(
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
                    modal.add(InteractionTarget.Child)
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

