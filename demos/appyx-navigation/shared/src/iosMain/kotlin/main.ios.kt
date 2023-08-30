import androidx.compose.runtime.Composable
import androidx.compose.ui.window.ComposeUIViewController
import com.bumble.appyx.navigation.navigation.IOSNodeHost
import com.bumble.appyx.navigation.node.container.ContainerNode

fun MainViewController() = ComposeUIViewController { App() }

@Composable
actual fun NodeHost() {
    IOSNodeHost { buildContext ->
        ContainerNode(
            buildContext = buildContext,
        )
    }
}