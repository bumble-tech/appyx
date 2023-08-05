package com.bumble.appyx.interactions.core.ui

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.compositionLocalOf
import com.bumble.appyx.interactions.core.ui.property.MotionProperty

@Suppress("CompositionLocalAllowlist")
val LocalMotionProperties = compositionLocalOf<List<MotionProperty<*, *>>?> { null }

val LocalBoxScope = compositionLocalOf<BoxScope?> { null }

