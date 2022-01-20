package com.bumble.appyx.v2.core.state

/**
 * Bundle for future state restoration.
 * Values should be supported by [androidx.compose.runtime.saveable.SaverScope.canBeSaved].
 */
typealias SavedStateMap = Map<String, Any?>
