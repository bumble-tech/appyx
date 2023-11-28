import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import com.bumble.appyx.navigation.integration.IosNodeHost
import com.bumble.appyx.navigation.integration.MainIntegrationPoint
import com.bumble.appyx.navigation.navigator.LocalNavigator
import com.bumble.appyx.navigation.navigator.Navigator
import com.bumble.appyx.navigation.node.root.RootNode
import com.bumble.appyx.navigation.ui.AppyxSampleAppTheme
import kotlinx.coroutines.flow.flowOf
import platform.Foundation.NSURL

private val integrationPoint = MainIntegrationPoint()
private val navigator = Navigator()

@Suppress("FunctionNaming")
fun MainViewController() = ComposeUIViewController {
    AppyxSampleAppTheme {
        Scaffold(
            modifier = Modifier
                .background(Color.Black)
//                .padding(top = 60.dp)
        ) {
            Box {

                Box(modifier = Modifier.fillMaxSize()) {
                    CompositionLocalProvider(LocalNavigator provides navigator) {
                        IosNodeHost(
                            modifier = Modifier,
                            onBackPressedEvents = flowOf(),
                            integrationPoint = integrationPoint
                        ) { buildContext ->
                            RootNode(
                                buildContext = buildContext,
                                plugins = listOf(navigator),
                            )
                        }
                    }
                }
            }
        }
    }
}.also { uiViewController ->
    integrationPoint.setViewController(uiViewController)
}

@Suppress("FunctionNaming")
fun handleDeepLinks(url: NSURL) {
    if (url.scheme == "appyx") {
        when (url.host) {
            // xcrun simctl openurl booted 'appyx://randomcake'
            ("randomcake") -> navigator.goToARandomCakeWithDummyUser()

            // xcrun simctl openurl booted 'appyx://randomcake-wait'
            ("randomcake-wait") -> navigator.goToARandomCake()
        }
    }
}
