package com.bumble.appyx.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.bumble.appyx.app.node.samples.SamplesContainerNode
import com.bumble.appyx.app.ui.AppyxSampleAppTheme
import com.bumble.appyx.core.integration.NodeHost
import com.bumble.appyx.core.integrationpoint.NodeActivity
import com.bumble.appyx.dagger.hilt.rememberNodeFactoryProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
                    Column {
                        // nodeFactoryProvider is only required if using the dagger-hilt library
                        val nodeFactoryProvider = rememberNodeFactoryProvider()

                        NodeHost(integrationPoint = appyxIntegrationPoint) {
                            SamplesContainerNode(
                                buildContext = it,
                                nodeFactoryProvider = nodeFactoryProvider,
                            )
                        }
                    }
                }
            }
        }
    }
}
