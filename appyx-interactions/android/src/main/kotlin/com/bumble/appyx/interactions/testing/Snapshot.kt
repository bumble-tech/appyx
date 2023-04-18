package com.bumble.appyx.interactions.testing

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onRoot
import androidx.test.annotation.ExperimentalTestApi
import androidx.test.core.graphics.writeToTestStorage

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalTestApi
fun ComposeTestRule.snapshot(name: String) =
    this
        .onRoot()
        .captureToImage()
        .asAndroidBitmap()
        .writeToTestStorage(name)
