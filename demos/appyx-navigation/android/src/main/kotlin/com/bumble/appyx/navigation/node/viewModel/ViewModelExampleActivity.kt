package com.bumble.appyx.navigation.node.viewModel

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.bumble.appyx.navigation.integration.NodeHost
import com.bumble.appyx.navigation.node.node
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
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colorScheme.background) {
                    NodeHost(
                        lifecycle = AndroidLifecycle(LocalLifecycleOwner.current.lifecycle),
                        integrationPoint = appyxV2IntegrationPoint,
                    ) {
                        node(buildContext = it) { modifier ->
                            CounterScreen(
                                viewModel,
                                modifier
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CounterScreen(
    viewModel: ViewModelExample,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState(initial = UiState(0))

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "Counter: ${uiState.counter}",
            fontSize = 45.sp
        )

        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = { viewModel.incrementCounter() }
        ) {
            Text("Increment")
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
    AppyxSampleAppTheme {
        CounterScreen(ViewModelExample())
    }
}
