package com.bumble.appyx.interactions

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.ExperimentalUnitApi
import com.bumble.appyx.interactions.bottomnav.appyxBottomNavItems
import com.bumble.appyx.interactions.theme.AppyxTheme
import com.bumble.appyx.interactions.theme.appyx_dark

@ExperimentalMaterialApi
@ExperimentalUnitApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Suppress("MagicNumber")
class MainActivity : ComponentActivity() {

    @Suppress("ComplexMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppyxTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = appyx_dark
                ) {
                    var content by remember { mutableStateOf(1) }

                    var selectedItem by remember { mutableStateOf(0) }
                    androidx.compose.material3.Scaffold(
                        content = { paddingValues: PaddingValues ->
                            Box(modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues))
                            {
                                appyxBottomNavItems[selectedItem].content()
                            }
                        },
                        bottomBar = {
                            NavigationBar {
                                appyxBottomNavItems.forEachIndexed { index, item ->
                                    NavigationBarItem(
                                        icon = { item.icon(selectedItem == index) },
                                        label = { item.text(selectedItem == index) },
                                        selected = selectedItem == index,
                                        onClick = { selectedItem = index }
                                    )
                                }
                            }
                        }
                    )

//                    Column {
//                        LazyRow {
//                            items(count = 8, itemContent = { item ->
//                                when (item) {
//                                    0 -> Button({ content = 1 }) { Text("1") }
//                                    1 -> Button({ content = 2 }) { Text("2") }
//                                    2 -> Button({ content = 3 }) { Text("3") }
//                                    3 -> Button({ content = 4 }) { Text("4") }
//                                    4 -> Button({ content = 5 }) { Text("5") }
//                                    5 -> Button({ content = 6 }) { Text("6") }
//                                    6 -> Button({ content = 7 }) { Text("7") }
//                                    7 -> Button({ content = 8 }) { Text("8") }
//                                }
//                            })
//                        }
//                        when (content) {
//                            1 -> DatingCards()
//                            2 -> SpotlightExperimentInVertical { SpotlightStack3D(it) }
//                            3 -> SpotlightExperiment { SpotlightSliderScale(it) }
//                            4 -> SpotlightExperiment { SpotlightSliderRotation(it) }
//                            5 -> SpotlightExperiment { SpotlightFader(it) }
//                            6 -> TestDriveExperiment()
//                            7 -> Puzzle15()
//                            8 -> ModalExperiment { ModalMotionController(it) }
//                            else -> SpotlightExperiment { SpotlightSlider(it) }
//                        }
//                    }
                }
            }
        }
    }
}


