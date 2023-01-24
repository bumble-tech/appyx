package com.bumble.appyx.navigation.state

import androidx.compose.runtime.saveable.SaverScope

interface MutableSavedStateMap : MutableMap<String, Any?> {
    val saverScope: SaverScope
}
