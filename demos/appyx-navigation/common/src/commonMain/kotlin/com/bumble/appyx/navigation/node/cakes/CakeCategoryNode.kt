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
import com.bumble.appyx.navigation.node.cakes.CakeCategoryNode.NavTarget
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

class CakeCategoryNode(
    buildContext: BuildContext,
    private val backStack: BackStack<NavTarget> = BackStack(
        model = BackStackModel(
            initialTargets = listOf(NavTarget.CakeList),
            savedStateMap = buildContext.savedStateMap,
        ),
        visualisation = { BackStackSlider(it) }
    )
) : ParentNode<NavTarget>(
    buildContext = buildContext,
    appyxComponent = backStack
) {
    sealed class NavTarget : Parcelable {
        @Parcelize
        object CakeList : NavTarget()
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is NavTarget.CakeList -> CakeListNode(buildContext)
        }

    @Composable
    override fun View(modifier: Modifier) {
        AppyxComponent(
            appyxComponent = backStack,
            modifier = modifier
        )
    }
}
