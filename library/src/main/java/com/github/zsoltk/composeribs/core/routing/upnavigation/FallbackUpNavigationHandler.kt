package com.github.zsoltk.composeribs.core.routing.upnavigation

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

val LocalFallbackUpNavigationHandler: ProvidableCompositionLocal<FallbackUpNavigationHandler> =
    compositionLocalOf { throw IllegalStateException("FallbackUpNavigationHandler is not initialised") }

fun interface FallbackUpNavigationHandler {
    fun handleUpNavigation()
}
