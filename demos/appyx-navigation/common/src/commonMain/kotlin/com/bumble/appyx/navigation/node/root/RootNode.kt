package com.bumble.appyx.navigation.node.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.replace
import com.bumble.appyx.components.backstack.ui.fader.BackStackFader
import com.bumble.appyx.interactions.core.plugin.Plugin
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.loggedout.LoggedOutNode
import com.bumble.appyx.navigation.node.main.MainNode
import com.bumble.appyx.navigation.node.profile.User
import com.bumble.appyx.navigation.node.root.RootNode.NavTarget
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import kotlinx.coroutines.delay

class RootNode(
    buildContext: BuildContext,
    private val backStack: BackStack<NavTarget> = BackStack(
        model = BackStackModel(
            initialTargets = listOf(
                NavTarget.Main(
                    /**
                     * Passing an initial dummy user. This is just for demonstration purposes
                     * so that we can show the main part of the app directly after launch
                     * and not the login screen.
                     * Of course in real life you would want to fetch an authenticated user first.
                     */
                    user = User.Dummy
                )
            ),
            savedStateMap = buildContext.savedStateMap,
        ),
        visualisation = { BackStackFader(it) }
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
        data class Main(
            /**
             * For simplicity we're passing the user directly as a navigation key (it's Parcelable).
             * For any real-life object with lots of data, you wouldn't to do this, but rather pass
             * just a simple user id (Int or Long) and fetch the user where needed from a Repository.
             */
            val user: User
        ) : NavTarget()
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is NavTarget.LoggedOut -> LoggedOutNode(
                buildContext = buildContext,
                onLogin = { user -> backStack.replace(NavTarget.Main(user)) }
            )
            is NavTarget.Main -> MainNode(
                buildContext = buildContext,
                user = navTarget.user,
                onLogout = { backStack.replace(NavTarget.LoggedOut) }
            )
        }

    @Composable
    override fun View(modifier: Modifier) {
        AppyxComponent(
            appyxComponent = backStack,
            modifier = Modifier
        )
    }

    suspend fun goToMain(user: User, delay: Long = 0): MainNode {
        backStack.replace(NavTarget.Main(user))
        delay(delay)
        return attachChild {}
    }
}

