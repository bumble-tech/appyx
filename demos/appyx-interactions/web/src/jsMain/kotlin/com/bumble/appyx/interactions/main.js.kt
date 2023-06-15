package com.bumble.appyx.interactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.window.Window
import com.bumble.appyx.components.testdrive.TestDriveExperiment
import com.bumble.appyx.components.testdrive.ui.md_amber_500
import com.bumble.appyx.components.testdrive.ui.md_blue_500
import com.bumble.appyx.components.testdrive.ui.md_blue_grey_500
import com.bumble.appyx.components.testdrive.ui.md_cyan_500
import com.bumble.appyx.components.testdrive.ui.md_grey_500
import com.bumble.appyx.components.testdrive.ui.md_indigo_500
import com.bumble.appyx.components.testdrive.ui.md_light_blue_500
import com.bumble.appyx.components.testdrive.ui.md_light_green_500
import com.bumble.appyx.components.testdrive.ui.md_lime_500
import com.bumble.appyx.components.testdrive.ui.md_pink_500
import com.bumble.appyx.components.testdrive.ui.md_teal_500
import org.jetbrains.skiko.wasm.onWasmReady

val manatee = Color(0xFF8D99AE)
val silver_sand = Color(0xFFBDC6D1)
val sizzling_red = Color(0xFFF05D5E)
val atomic_tangerine = Color(0xFFF0965D)

val colors = listOf(
    manatee,
    sizzling_red,
    atomic_tangerine,
    silver_sand,
    md_pink_500,
    md_indigo_500,
    md_blue_500,
    md_light_blue_500,
    md_cyan_500,
    md_teal_500,
    md_light_green_500,
    md_lime_500,
    md_amber_500,
    md_grey_500,
    md_blue_grey_500
)

enum class InteractionTarget {
    Child1
}

fun main() {
    onWasmReady {
        Window("Appyx") {
            var size by remember { mutableStateOf(IntSize.Zero) }
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged { size = it }
            ) {
                if (size != IntSize.Zero) {
                    TestDriveExperiment(
                        screenWidthPx = size.width,
                        screenHeightPx = size.height,
                        element = InteractionTarget.Child1,
                        modifier = Modifier.fillMaxSize().background(Color.Black),
                    )
                }
            }
        }
    }
}
