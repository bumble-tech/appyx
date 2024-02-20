package com.bumble.appyx.demos.sandbox.navigation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.bumble.appyx.demos.sandbox.navigation.node.container.MainNavNode
import com.bumble.appyx.demos.sandbox.navigation.ui.AppyxSampleAppTheme
import com.bumble.appyx.navigation.integration.NodeActivity
import com.bumble.appyx.navigation.integration.NodeHost
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.platform.AndroidLifecycle

@ExperimentalUnitApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
class MainActivity : NodeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            AppyxSampleAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colorScheme.background) {
                    NodeHost(
                        lifecycle = AndroidLifecycle(LocalLifecycleOwner.current.lifecycle),
                        integrationPoint = appyxIntegrationPoint,
                    ) {
                        MainNavNode(
                            nodeContext = it,
                        )
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
            MainNavNode(
                nodeContext = NodeContext.root(null),
            ).Compose()
        }
    }
}
