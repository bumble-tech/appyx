package com.bumble.appyx.navigation.node.cakes

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.ui.slider.BackStackSlider
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.cakes.CakeCategoryNode.InteractionTarget
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

class CakeCategoryNode(
    buildContext: BuildContext,
    private val backStack: BackStack<InteractionTarget> = BackStack(
        model = BackStackModel(
            initialTargets = listOf(InteractionTarget.CakeList),
            savedStateMap = buildContext.savedStateMap,
        ),
        visualisation = { BackStackSlider(it) }
    )
) : ParentNode<InteractionTarget>(
    buildContext = buildContext,
    appyxComponent = backStack
) {
    sealed class InteractionTarget : Parcelable {
        @Parcelize
        object CakeList : InteractionTarget()
    }

    override fun resolve(interactionTarget: InteractionTarget, buildContext: BuildContext): Node =
        when (interactionTarget) {
            is InteractionTarget.CakeList -> CakeListNode(buildContext)
        }

    @Composable
    override fun View(modifier: Modifier) {
        AppyxComponent(
            appyxComponent = backStack,
            modifier = Modifier
        )
    }
}
