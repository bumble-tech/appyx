package com.github.zsoltk.composeribs.core.state

/**
 * Bundle for future state restoration.
 * Values should be supported by [androidx.compose.runtime.saveable.SaverScope.canBeSaved].
 */
typealias SavedStateMap = Map<String, Any>
