package com.bumble.appyx.navigation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.bumble.appyx.navigation.integration.NodeActivity
import com.bumble.appyx.navigation.integration.NodeHost
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.navigator.LocalNavigator
import com.bumble.appyx.navigation.node.main.MainNode
import com.bumble.appyx.navigation.platform.AndroidLifecycle
import com.bumble.appyx.navigation.plugin.NodeReadyObserver
import com.bumble.appyx.navigation.ui.AppyxSampleAppTheme
import com.bumble.appyx.navigation.navigator.Navigator
import com.bumble.appyx.navigation.navigator.LocalNavigator

@ExperimentalUnitApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
class MainActivity : NodeActivity() {

    private val navigator = Navigator()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            AppyxSampleAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colorScheme.background) {
                    CompositionLocalProvider(LocalNavigator provides navigator) {
                        NodeHost(
                            lifecycle = AndroidLifecycle(LocalLifecycleOwner.current.lifecycle),
                            integrationPoint = appyxV2IntegrationPoint,
                        ) {
                            MainNode(
                                buildContext = it,
                                plugins = listOf(navigator)
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
@ExperimentalUnitApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
fun DefaultPreview() {
    AppyxSampleAppTheme {
        Column {
            MainNode(
                buildContext = BuildContext.root(null),
            ).Compose()
        }
    }
}
