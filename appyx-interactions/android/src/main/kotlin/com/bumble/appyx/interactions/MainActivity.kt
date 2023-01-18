package com.bumble.appyx.interactions

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.sample.BackStackExperimentDebug
import com.bumble.appyx.interactions.sample.DatingCards
import com.bumble.appyx.interactions.sample.PromoterExperiment
import com.bumble.appyx.interactions.sample.SpotlightExperiment
import com.bumble.appyx.interactions.sample.SpotlightExperimentDebug
import com.bumble.appyx.interactions.theme.AppyxTheme
import com.bumble.appyx.interactions.theme.appyx_dark

@ExperimentalMaterialApi
@ExperimentalUnitApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppyxTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = appyx_dark
                ) {
                    var content by remember { mutableStateOf(0) }
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button({ content = 0 }) { Text("1") }
                            Button({ content = 1 }) { Text("2") }
                            Button({ content = 2 }) { Text("3") }
                            Button({ content = 3 }) { Text("4") }
                            Button({ content = 4 }) { Text("5") }
                        }
                        when (content) {
                            0 -> DatingCards()
                            1 -> SpotlightExperiment()
                            2 -> SpotlightExperimentDebug()
                            3 -> BackStackExperimentDebug()
                            4 -> PromoterExperiment()
                            else -> SpotlightExperiment()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}
