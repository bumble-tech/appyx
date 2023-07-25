package com.bumble.appyx.interactions.core.ui

import androidx.compose.runtime.compositionLocalOf
import com.bumble.appyx.interactions.core.ui.property.MotionProperty

val LocalMotionProperties = compositionLocalOf<List<MotionProperty<*, *>>?> { null }
