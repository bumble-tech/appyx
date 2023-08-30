import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.navigation.ui.AppyxSampleAppTheme

@Composable
internal fun App() {
    AppyxSampleAppTheme {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            NodeHost()
        }
    }
}

@Composable
expect fun NodeHost()