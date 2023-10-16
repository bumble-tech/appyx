package com.bumble.appyx.navigation.node.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.replace
import com.bumble.appyx.components.backstack.ui.slider.BackStackSlider
import com.bumble.appyx.interactions.core.plugin.Plugin
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.loggedout.LoggedOutNode
import com.bumble.appyx.navigation.node.main.MainNode
import com.bumble.appyx.navigation.node.root.RootNode.NavTarget
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import kotlinx.coroutines.delay

class RootNode(
    buildContext: BuildContext,
    private val backStack: BackStack<NavTarget> = BackStack(
        model = BackStackModel(
            initialTargets = listOf(NavTarget.Main),
            savedStateMap = buildContext.savedStateMap,
        ),
        visualisation = { BackStackSlider(it) }
    ),
    plugins: List<Plugin> = listOf(),
) : ParentNode<NavTarget>(
    buildContext = buildContext,
    appyxComponent = backStack,
    plugins = plugins
) {
    sealed class NavTarget : Parcelable {
        @Parcelize
        object LoggedOut : NavTarget()

        @Parcelize
        object Main : NavTarget()
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is NavTarget.LoggedOut -> LoggedOutNode(buildContext)
            is NavTarget.Main -> MainNode(buildContext)
        }

    @Composable
    override fun View(modifier: Modifier) {
        AppyxComponent(
            appyxComponent = backStack,
            modifier = Modifier
        )
    }

    suspend fun goToMain(delay: Long = 0): MainNode {
        backStack.replace(NavTarget.Main)
        delay(delay)
        return attachChild {}
    }
}

