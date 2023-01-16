import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.bumble.appyx.interactions.App


fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
