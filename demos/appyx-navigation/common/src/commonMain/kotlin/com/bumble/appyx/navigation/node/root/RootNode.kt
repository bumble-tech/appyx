package com.bumble.appyx.navigation.node.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.replace
import com.bumble.appyx.components.backstack.ui.fader.BackStackFader
import com.bumble.appyx.interactions.core.plugin.Plugin
import com.bumble.appyx.navigation.composable.AppyxNavigationContainer
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.loggedout.LoggedOutNode
import com.bumble.appyx.navigation.node.main.MainNode
import com.bumble.appyx.navigation.node.profile.User
import com.bumble.appyx.navigation.node.root.RootNode.NavTarget
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

class RootNode(
    nodeContext: NodeContext,
    allowDummyLogin: Boolean = true,
    private val backStack: BackStack<NavTarget> = BackStack(
        model = BackStackModel(
            initialTargets = listOf(
                when (allowDummyLogin) {
                    true -> NavTarget.Main(user = User.Dummy)
                    false -> NavTarget.LoggedOut
                }
            ),
            savedStateMap = nodeContext.savedStateMap,
        ),
        visualisation = { BackStackFader(it) }
    ),
    plugins: List<Plugin> = listOf(),
) : ParentNode<NavTarget>(
    nodeContext = nodeContext,
    appyxComponent = backStack,
    plugins = plugins
) {
    sealed class NavTarget : Parcelable {
        @Parcelize
        object LoggedOut : NavTarget()

        @Parcelize
        data class Main(
            /**
             * For simplicity we're passing the user directly as a navigation key (it's Parcelable).
             * For any real-life object with lots of data, you wouldn't to do this, but rather pass
             * just a simple user id (Int or Long) and fetch the user where needed from a Repository.
             */
            val user: User
        ) : NavTarget()
    }

    override fun buildChildNode(navTarget: NavTarget, nodeContext: NodeContext): Node =
        when (navTarget) {
            is NavTarget.LoggedOut -> LoggedOutNode(
                nodeContext = nodeContext,
                onLogin = { user -> onLogin(user) }
            )
            is NavTarget.Main -> MainNode(
                nodeContext = nodeContext,
                user = navTarget.user,
                onLogout = { backStack.replace(NavTarget.LoggedOut) }
            )
        }

    @Composable
    override fun View(modifier: Modifier) {
        AppyxNavigationContainer(
            appyxComponent = backStack,
            modifier = Modifier
        )
    }

    fun onLogin(user: User): RootNode {
        backStack.replace(NavTarget.Main(user))
        return this
    }


    suspend fun waitForMainAttached(): MainNode =
        waitForChildAttached()
}

