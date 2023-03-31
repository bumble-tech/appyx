package com.bumble.appyx.interactions.core.state

import androidx.compose.runtime.saveable.SaverScope

interface MutableSavedStateMap : MutableMap<String, Any?> {
    val saverScope: SaverScope
}
