package com.github.zsoltk.composeribs.core

/**
 * Bundle for future state restoration.
 * Values should be supported by [androidx.compose.runtime.saveable.SaverScope.canBeSaved].
 */
typealias SavedStateMap = Map<String, Any>
