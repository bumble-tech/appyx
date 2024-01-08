package com.bumble.appyx.navigation

import android.content.Intent
import android.net.Uri
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
import com.bumble.appyx.navigation.navigator.Navigator
import com.bumble.appyx.navigation.node.root.RootNode
import com.bumble.appyx.navigation.platform.AndroidLifecycle
import com.bumble.appyx.navigation.plugin.NodeReadyObserver
import com.bumble.appyx.navigation.ui.AppyxSampleAppTheme
import com.google.android.material.snackbar.Snackbar

@ExperimentalUnitApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
class MainActivity : NodeActivity() {

    private val navigator = Navigator()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        checkDebugBuild()

        setContent {
            AppyxSampleAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colorScheme.background) {
                    CompositionLocalProvider(LocalNavigator provides navigator) {
                        NodeHost(
                            lifecycle = AndroidLifecycle(LocalLifecycleOwner.current.lifecycle),
                            integrationPoint = appyxV2IntegrationPoint,
                        ) {
                            RootNode(
                                buildContext = it,
                                /**
                                 * For demonstration purposes only.
                                 * When launching directly, we won't have a deep link.
                                 * We allow skipping the login screen so that we can immediately
                                 * show the internal screens that can only be accessed
                                 * with a User object.
                                 *
                                 * When launching from a deep link however, we'll want to
                                 * demonstrate one of the use-cases of waiting for login though,
                                 * so we won't do the dummy login.
                                 */
                                allowDummyLogin = (intent?.data == null),
                                plugins = listOf(navigator, NodeReadyObserver {
                                    handleDeepLinks(intent?.data)
                                })
                            )
                        }
                    }
                }
            }
        }
    }

    private fun handleDeepLinks(uri: Uri?) {
        if (intent?.action == Intent.ACTION_VIEW) {
            when {
                // adb shell am start -a "android.intent.action.VIEW" -d "appyx://randomcake"
                (uri?.host == "randomcake") -> navigator.goToARandomCakeWithDummyUser()

                // adb shell am start -a "android.intent.action.VIEW" -d "appyx://randomcake-wait"
                (uri?.host == "randomcake-wait") -> navigator.goToARandomCake()
            }
        }
    }

    private fun checkDebugBuild() {
        if (BuildConfig.DEBUG) {
            val snackbar = Snackbar.make(
                findViewById(android.R.id.content),
                "Heads up! For better performance, run the app using the Release build.",
                Snackbar.LENGTH_INDEFINITE
            )
            snackbar.apply {
                setAction("OK") { snackbar.dismiss() }
                show()
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
            RootNode(
                buildContext = BuildContext.root(null),
            ).Compose()
        }
    }
}
