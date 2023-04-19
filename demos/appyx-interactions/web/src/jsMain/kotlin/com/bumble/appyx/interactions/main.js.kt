package com.bumble.appyx.interactions

import androidx.compose.material.Text
import androidx.compose.ui.window.Window
import org.jetbrains.skiko.wasm.onWasmReady

fun main() {
    onWasmReady {
        Window("Appyx") {
            Text("Hello from Appyx Transitions")
        }
    }
}