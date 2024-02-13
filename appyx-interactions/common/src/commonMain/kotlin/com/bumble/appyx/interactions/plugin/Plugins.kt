package com.bumble.appyx.interactions.plugin

import com.bumble.appyx.interactions.state.MutableSavedStateMap

/**
 * Bundle for future state restoration.
 * Result should be supported by [androidx.compose.runtime.saveable.SaverScope.canBeSaved].
 */
interface SavesInstanceState : Plugin {
    fun saveInstanceState(state: MutableSavedStateMap) {}
}

interface Plugin
