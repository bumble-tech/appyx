package com.bumble.appyx.interactions.gesture

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

@Stable
fun interface GestureValidator {
    fun isGestureValid(position: Offset, boundingBox: Rect): Boolean

    companion object {
        val defaultValidator = GestureValidator { position, boundingBox -> boundingBox.contains(position) }

        @Stable
        val permissiveValidator = GestureValidator { _, _ -> true }
    }
}

