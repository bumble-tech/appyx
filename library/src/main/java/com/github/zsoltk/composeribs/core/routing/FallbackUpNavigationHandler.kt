package com.github.zsoltk.composeribs.core.routing

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

val LocalFallbackUpNavigationHandler: ProvidableCompositionLocal<FallbackUpNavigationHandler> =
    compositionLocalOf { throw IllegalStateException("FallbackUpNavigationHandler is not initialised") }

class FallbackUpNavigationHandler(
    private val onUpNavigation: () -> Unit
) {

    fun handle() {
        onUpNavigation()
    }
}