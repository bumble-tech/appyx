package com.bumble.appyx.demos

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.window.Window
import com.bumble.appyx.components.experimental.puzzle15.ui.Puzzle15Ui
import org.jetbrains.skiko.wasm.onWasmReady

val manatee = Color(0xFF8D99AE)
val silver_sand = Color(0xFFBDC6D1)
val sizzling_red = Color(0xFFF05D5E)
val atomic_tangerine = Color(0xFFF0965D)
val appyx_dark = Color(0xFF1F2126)

val colors = listOf(
    manatee,
    sizzling_red,
    atomic_tangerine,
    silver_sand,
    appyx_dark
)

fun main() {
    onWasmReady {
        Window("Puzzle15") {
            val requester = remember { FocusRequester() }
            var size by remember { mutableStateOf(IntSize.Zero) }
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged { size = it }
            ) {
                Puzzle15Ui(
                    screenWidthPx = size.width,
                    screenHeightPx = size.height,
                    colors = colors,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .focusRequester(requester)
                        .focusable(),
                )
            }

            LaunchedEffect(Unit) {
                requester.requestFocus()
            }
        }
    }
}
