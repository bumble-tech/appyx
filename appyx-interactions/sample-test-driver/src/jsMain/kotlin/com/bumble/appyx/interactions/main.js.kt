package com.bumble.appyx.interactions

import androidx.compose.ui.window.Window
import org.jetbrains.skiko.wasm.onWasmReady

fun main() {
    onWasmReady {
        Window("Appyx") {
            TestDriveExperiment(
                ignoreChildrenVisibility = true,
            )
        }
    }
}
