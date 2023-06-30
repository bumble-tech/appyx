package com.bumble.appyx.demos.experimental.datingcards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import com.bumble.appyx.demos.common.color_dark
import org.jetbrains.skiko.wasm.onWasmReady


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
                    DatingCardsSample(
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
