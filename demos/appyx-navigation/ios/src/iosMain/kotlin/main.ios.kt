import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.ComposeUIViewController
import androidx.compose.ui.zIndex
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.ui.slider.BackStackSlider
import com.bumble.appyx.navigation.integration.IOSNodeHost
import com.bumble.appyx.navigation.node.container.ContainerNode
import com.bumble.appyx.navigation.ui.AppyxSampleAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

val backEvents: Channel<Unit> = Channel()

fun MainViewController() = ComposeUIViewController {
    AppyxSampleAppTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            BackButton(rememberCoroutineScope())

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

@Composable
private fun BackButton(coroutineScope: CoroutineScope) {
    IconButton(
        onClick = {
            coroutineScope.launch {
                backEvents.send(Unit)
            }
        },
        modifier = Modifier.zIndex(5f)
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            tint = Color.White,
            contentDescription = ""
        )
    }
}