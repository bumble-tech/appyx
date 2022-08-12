package com.bumble.appyx.sample.navigtion.compose

import android.os.Bundle
import android.os.Parcelable
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.integration.NodeHost
import com.bumble.appyx.core.integrationpoint.LocalIntegrationPoint
import com.bumble.appyx.core.integrationpoint.NodeActivity
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.node
import com.bumble.appyx.routingsource.backstack.BackStack
import kotlinx.parcelize.Parcelize

class MainActivity : NodeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 'integrationPoint' must be provided to ensure it can be accessed from within the
            // Jetpack compose navigation graph.
            CompositionLocalProvider(
                LocalIntegrationPoint provides integrationPoint,
            ) {
                Surface(color = MaterialTheme.colors.background) {
                    Column {
                        NavigationRoot()
                    }
                }
            }
        }
    }
}

@Composable
fun NavigationRoot() {
    Column(Modifier.fillMaxSize()) {
        Text(
            text = "Navigation Compose interop example",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp),
            color = Color.Black,
            style = MaterialTheme.typography.subtitle1
        )

        val googleNavController = rememberNavController()
        NavHost(navController = googleNavController, startDestination = "google-route") {
            composable("google-route") {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Jetpack navigation screen")
                    Button(onClick = { googleNavController.navigate("appyx-route") }) {
                        Text("Navigate to Appyx")
                    }
                }
            }
            composable("appyx-route") {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Appyx screen")
                    Button(onClick = { googleNavController.navigate("google-route") }) {
                        Text("Navigate to Google")
                    }
                    NodeHost(integrationPoint = LocalIntegrationPoint.current) {
                        ContainerNode(buildContext = it)
                    }
                }
            }
        }
    }
}

private class ContainerNode(
    buildContext: BuildContext,
    private val backStack: BackStack<Routing> = BackStack(
        initialElement = Routing.Main,
        savedStateMap = buildContext.savedStateMap,
    )
) : ParentNode<ContainerNode.Routing>(
    routingSource = backStack,
    buildContext = buildContext,
) {

    sealed class Routing : Parcelable {
        @Parcelize
        object Main : Routing()
    }

    override fun resolve(routing: Routing, buildContext: BuildContext): Node =
        when (routing) {
            is Routing.Main -> node(buildContext) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Hello from Appyx!")
                }
            }
        }

    @Composable
    override fun View(modifier: Modifier) {
        Children(
            modifier = modifier.fillMaxSize(),
            routingSource = backStack
        ) {
            children<Routing> { child ->
                child()
            }
        }
    }
}
