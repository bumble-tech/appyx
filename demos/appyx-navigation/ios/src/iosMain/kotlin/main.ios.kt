import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import androidx.compose.ui.zIndex
import com.bumble.appyx.navigation.integration.IosNodeHost
import com.bumble.appyx.navigation.node.container.ContainerNode
import com.bumble.appyx.navigation.ui.AppyxSampleAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

val backEvents: Channel<Unit> = Channel()

@Suppress("FunctionNaming")
fun MainViewController() = ComposeUIViewController {
    AppyxSampleAppTheme {
        val coroutineScope = rememberCoroutineScope()
        Scaffold(
            modifier = Modifier
                .background(Color.Black)
                .padding(top = 60.dp)
        ) {
            Box {
                BackButton(coroutineScope)

                Box(modifier = Modifier.fillMaxSize()) {
                    IosNodeHost(
                        modifier = Modifier,
                        onBackPressedEvents = backEvents.receiveAsFlow()
                    ) { buildContext ->
                        ContainerNode(
                            buildContext = buildContext,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BackButton(coroutineScope: CoroutineScope) {
    IconButton(
        onClick = {
            coroutineScope.launch {
                backEvents.send(Unit)
            }
        },
        modifier = Modifier.zIndex(99f)
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            tint = Color.White,
            contentDescription = "Go Back",
        )
    }
}
