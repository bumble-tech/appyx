package com.bumble.appyx.interactions.testing

import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onRoot
import androidx.test.core.graphics.writeToTestStorage

fun ComposeTestRule.snapshot(name: String) =
    this
        .onRoot()
        .captureToImage()
        .asAndroidBitmap()
        .writeToTestStorage(name)
