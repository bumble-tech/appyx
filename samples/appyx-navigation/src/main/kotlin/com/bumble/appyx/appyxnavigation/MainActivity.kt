package com.bumble.appyx.appyxnavigation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.bumble.appyx.appyxnavigation.node.container.ContainerNode
import com.bumble.appyx.appyxnavigation.node.datingcards.DatingCardsNode
import com.bumble.appyx.appyxnavigation.ui.AppyxSampleAppTheme
import com.bumble.appyx.navigation.integration.NodeHost
import com.bumble.appyx.navigation.integrationpoint.NodeActivity
import com.bumble.appyx.navigation.modality.BuildContext

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
                        NodeHost(integrationPoint = appyxIntegrationPoint) {
                            DatingCardsNode(buildContext = it)
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
            ContainerNode(buildContext = BuildContext.root(null)).Compose()
        }
    }
}
