package com.bumble.appyx.interactions.ui

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.compositionLocalOf
import com.bumble.appyx.interactions.ui.property.MotionProperty

@Suppress("CompositionLocalAllowlist")
val LocalMotionProperties = compositionLocalOf<List<MotionProperty<*, *>>?> { null }

@Suppress("CompositionLocalAllowlist")
val LocalBoxScope = compositionLocalOf<BoxScope?> { null }

