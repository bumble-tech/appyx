package com.bumble.appyx.navigation.node.main

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.ui.slider.BackStackSlider
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.main.MainNode.InteractionTarget
import com.bumble.appyx.navigation.node.node
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

class MainNode(
    buildContext: BuildContext,
    private val backStack: BackStack<InteractionTarget> = BackStack(
        model = BackStackModel(
            initialTargets = listOf(InteractionTarget.MainChild),
            savedStateMap = buildContext.savedStateMap,
        ),
        motionController = { BackStackSlider(it) }
    )
) : ParentNode<InteractionTarget>(
    buildContext = buildContext,
    appyxComponent = backStack
) {
    sealed class InteractionTarget : Parcelable {
        @Parcelize
        object MainChild : InteractionTarget()
    }

    override fun resolve(interactionTarget: InteractionTarget, buildContext: BuildContext): Node =
        when (interactionTarget) {
            is InteractionTarget.MainChild -> node(buildContext) {
                Text("Main Node")
            }
        }

    @Composable
    override fun View(modifier: Modifier) {
        AppyxComponent(
            appyxComponent = backStack,
            modifier = Modifier
        )
    }
}
