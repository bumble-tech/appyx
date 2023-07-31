import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import theme.DarkColorPalette
import theme.shapes

@Composable
internal fun App() {
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

expect fun getPlatformName(): String

@Composable
private fun AppyxTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = DarkColorPalette,
        shapes = shapes,
        content = content
    )
}