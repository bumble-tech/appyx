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
import com.bumble.appyx.navigation.integration.IOSNodeHost
import com.bumble.appyx.navigation.node.container.ContainerNode
import com.bumble.appyx.navigation.ui.AppyxSampleAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import platform.UIKit.UIWindow
import platform.UIKit.safeAreaInsets

val backEvents: Channel<Unit> = Channel()

fun MainViewController() = ComposeUIViewController {
    AppyxSampleAppTheme {
        val coroutineScope = rememberCoroutineScope()
        Scaffold(
            modifier = Modifier
                .background(Color.Black)
                .padding(top = UIWindow().safeAreaInsets.size.dp)
        ) {
            Box {
                BackButton(coroutineScope)

                Box(modifier = Modifier.fillMaxSize()) {
                    IOSNodeHost(
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
            contentDescription = ""
        )
    }
}