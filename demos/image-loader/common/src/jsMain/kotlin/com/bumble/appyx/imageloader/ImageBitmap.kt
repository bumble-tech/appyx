package com.bumble.appyx.imageloader

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap

actual fun ByteArray.toImageBitmap(): ImageBitmap {
    // TODO This doesn't work. Fix this for web
    return org.jetbrains.skia.Image.makeFromEncoded(this).toComposeImageBitmap()
}
