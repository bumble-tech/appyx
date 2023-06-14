package com.bumble.appyx.sample.navigtion.compose

import android.os.Parcelable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.stable.backstack.BackStack
import com.bumble.appyx.components.stable.backstack.BackStackModel
import com.bumble.appyx.components.stable.backstack.ui.slider.BackStackSlider
import com.bumble.appyx.navigation.composable.Children
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.node
import kotlinx.parcelize.Parcelize

internal class ComposeNavigationContainerNode(
    buildContext: BuildContext,
    private val onGoogleNavigationClick: () -> Unit,
    private val backStack: BackStack<InteractionTarget> = BackStack(
        model = BackStackModel(
            initialTargets = listOf(InteractionTarget.Main),
            savedStateMap = buildContext.savedStateMap
        ),
        motionController = { BackStackSlider(it) }
    )
) : ParentNode<ComposeNavigationContainerNode.InteractionTarget>(
    interactionModel = backStack,
    buildContext = buildContext,
) {

    sealed class InteractionTarget : Parcelable {
        @Parcelize
        object Main : InteractionTarget()
    }

    override fun resolve(interactionTarget: InteractionTarget, buildContext: BuildContext): Node =
        when (interactionTarget) {
            is InteractionTarget.Main -> node(buildContext) {
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
    override fun View(modifier: Modifier) {
        Children(
            modifier = modifier.fillMaxWidth(),
            interactionModel = backStack
        )
    }
}
