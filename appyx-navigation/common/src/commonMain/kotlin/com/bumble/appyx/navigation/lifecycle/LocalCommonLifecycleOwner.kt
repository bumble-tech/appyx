package com.bumble.appyx.navigation.lifecycle

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

@Suppress("CompositionLocalAllowlist")
val LocalCommonLifecycleOwner: ProvidableCompositionLocal<CommonLifecycleOwner?> =
    compositionLocalOf { null }
