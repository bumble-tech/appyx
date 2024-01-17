package com.bumble.appyx.sample.navigtion.compose

import android.os.Parcelable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.ui.slider.BackStackSlider
import com.bumble.appyx.navigation.composable.AppyxNavigationContainer
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.node
import kotlinx.parcelize.Parcelize

internal class ComposeNavigationContainerNode(
    nodeContext: NodeContext,
    private val onGoogleNavigationClick: () -> Unit,
    private val backStack: BackStack<InteractionTarget> = BackStack(
        model = BackStackModel(
            initialTargets = listOf(InteractionTarget.Main),
            savedStateMap = nodeContext.savedStateMap
        ),
        visualisation = { BackStackSlider(it) }
    )
) : ParentNode<ComposeNavigationContainerNode.InteractionTarget>(
    appyxComponent = backStack,
    nodeContext = nodeContext,
) {

    sealed class InteractionTarget : Parcelable {
        @Parcelize
        object Main : InteractionTarget()
    }

    override fun buildChildNode(navTarget: InteractionTarget, nodeContext: NodeContext): Node =
        when (navTarget) {
            is InteractionTarget.Main -> node(nodeContext) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Appyx screen")
                    Button(onClick = { onGoogleNavigationClick() }) {
                        Text("Navigate to Google")
                    }
                }
            }
        }

    @Composable
    override fun Content(modifier: Modifier) {
        AppyxNavigationContainer(
            modifier = modifier.fillMaxWidth(),
            appyxComponent = backStack
        )
    }
}
