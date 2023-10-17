package com.bumble.appyx.navigation.node.viewModel

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.bumble.appyx.navigation.integration.NodeHost
import com.bumble.appyx.navigation.platform.AndroidLifecycle
import com.bumble.appyx.navigation.ui.AppyxSampleAppTheme
import com.bumble.appyx.utils.viewmodel.integration.ViewModelNodeActivity

@ExperimentalUnitApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
class ViewModelExampleActivity : ViewModelNodeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        val viewModel: ViewModelExample by viewModels()

        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            AppyxSampleAppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    NodeHost(
                        lifecycle = AndroidLifecycle(LocalLifecycleOwner.current.lifecycle),
                        integrationPoint = appyxV2IntegrationPoint,
                    ) {
                        ViewModelNodeExample(buildContext = it)
                    }
                }
            }
        }
    }
}
