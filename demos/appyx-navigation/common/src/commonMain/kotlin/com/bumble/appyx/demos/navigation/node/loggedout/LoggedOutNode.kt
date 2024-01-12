package com.bumble.appyx.demos.navigation.node.loggedout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.components.backstack.ui.parallax.BackStackParallax
import com.bumble.appyx.components.backstack.ui.slider.BackStackSlider
import com.bumble.appyx.demos.navigation.node.loggedout.LoggedOutNode.NavTarget
import com.bumble.appyx.demos.navigation.node.profile.User
import com.bumble.appyx.demos.navigation.platform.IOS_PLATFORM_NAME
import com.bumble.appyx.demos.navigation.platform.getPlatformName
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.navigation.composable.AppyxNavigationContainer
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.AbstractNode
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.node
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

class LoggedOutNode(
    nodeContext: NodeContext,
    private val onLogin: (User) -> Unit,
    private val backStack: BackStack<NavTarget> = BackStack(
        model = BackStackModel(
            initialTargets = listOf(NavTarget.Splash),
            savedStateMap = nodeContext.savedStateMap,
        ),
        visualisation = {
            if (getPlatformName() == IOS_PLATFORM_NAME) {
                BackStackParallax(it)
            } else {
                BackStackSlider(it)
            }
        },
        gestureFactory = {
            if (getPlatformName() == IOS_PLATFORM_NAME) {
                BackStackParallax.Gestures(it)
            } else {
                GestureFactory.Noop()
            }
        }
    )
) : Node<NavTarget>(
    nodeContext = nodeContext,
    appyxComponent = backStack
) {
    sealed class NavTarget : Parcelable {
        @Parcelize
        object Splash : NavTarget()

        @Parcelize
        object Login : NavTarget()
    }

    override fun buildChildNode(navTarget: NavTarget, nodeContext: NodeContext): AbstractNode =
        when (navTarget) {
            is NavTarget.Splash -> node(nodeContext) { modifier ->
                SplashScreen(modifier)
            }

            NavTarget.Login -> node(nodeContext) { modifier ->
                LoginScreen(modifier)
            }
        }

    @Composable
    override fun Content(modifier: Modifier) {
        AppyxNavigationContainer(
            appyxComponent = backStack,
            modifier = Modifier
        )
    }

    @Composable
    private fun SplashScreen(modifier: Modifier = Modifier) {
        Surface {
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome!",
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(modifier = Modifier.size(24.dp))
                Button(
                    onClick = {
                        backStack.push(NavTarget.Login)
                    }
                ) {
                    Text(
                        text = "Log in",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }

    @Composable
    private fun LoginScreen(modifier: Modifier = Modifier) {
        Surface {
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var name by remember { mutableStateOf("Cake lover") }

                Text(
                    text = "Login",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.size(24.dp))
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") }
                )
                Spacer(modifier = Modifier.size(24.dp))
                Button(
                    onClick = {
                        onLogin(
                            User(name = name)
                        )
                    }
                ) {
                    Text(
                        text = "Enter",
                    )
                }
            }
        }
    }
}

