package com.bumble.appyx.interactions.core.gesture

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

typealias GestureValidator = (Offset, Rect) -> Boolean

@Stable
val defaultValidator: GestureValidator = { position, boundingBox -> boundingBox.contains(position) }

@Stable
val permissiveValidator: GestureValidator = { _, _ -> true }
