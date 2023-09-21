import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import theme.DarkColorPalette

fun MainViewController() = ComposeUIViewController {
    AppyxTheme {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            BackStackParallaxSample(
                screenHeightPx = constraints.maxHeight,
                screenWidthPx = constraints.maxWidth,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun AppyxTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = DarkColorPalette,
        content = content
    )
}
