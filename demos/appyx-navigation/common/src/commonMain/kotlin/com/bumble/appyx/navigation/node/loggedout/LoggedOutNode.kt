package com.bumble.appyx.navigation.node.loggedout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.replace
import com.bumble.appyx.components.backstack.ui.slider.BackStackSlider
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.loggedout.LoggedOutNode.NavTarget
import com.bumble.appyx.navigation.node.node
import com.bumble.appyx.navigation.node.profile.User
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

class LoggedOutNode(
    buildContext: BuildContext,
    private val onLogin: (User) -> Unit,
    private val backStack: BackStack<NavTarget> = BackStack(
        model = BackStackModel(
            initialTargets = listOf(NavTarget.Splash),
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
        object Splash : NavTarget()

        @Parcelize
        object Login : NavTarget()
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is NavTarget.Splash -> node(buildContext) { modifier ->
                SplashScreen(modifier)
            }

            NavTarget.Login -> node(buildContext) { modifier ->
                LoginScreen(modifier)
            }
        }

    @Composable
    override fun View(modifier: Modifier) {
        AppyxComponent(
            appyxComponent = backStack,
            modifier = Modifier
        )
    }

    @Composable
    private fun SplashScreen(modifier: Modifier) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Splash",
            )
            Button(
                onClick = {
                    backStack.replace(NavTarget.Login)
                }
            ) {
                Text(
                    text = "Log in",
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun LoginScreen(modifier: Modifier) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var name by remember { mutableStateOf("Cake lover") }

            Text(
                text = "Login",
            )
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") }
            )
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

