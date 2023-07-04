package com.bumble.appyx.demos.incompletedrag

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import org.jetbrains.skiko.wasm.onWasmReady

val color_bright = Color(0xFFFFFFFF)
val color_dark = Color(0xFF353535)
val color_primary = Color(0xFFFFC629)
val color_secondary = Color(0xFFFE9763)
val color_tertiary = Color(0xFF855353)
val color_neutral1 = Color(0xFFD2D7DF)
val color_neutral2 = Color(0xFF8A897C)
val color_neutral3 = Color(0xFFD9E8ED)
val color_neutral4 = Color(0xFFBEA489)


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
                    IncompleteDrag(
                        screenWidthPx = size.width,
                        screenHeightPx = size.height,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color_dark)
                            .padding(
                                horizontal = 16.dp,
                                vertical = 16.dp
                            )
                    )
                }
            }
        }
    }
}
