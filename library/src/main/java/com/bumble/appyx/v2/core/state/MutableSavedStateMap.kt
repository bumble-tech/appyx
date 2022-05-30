package com.bumble.appyx.v2.core.state

import androidx.compose.runtime.saveable.SaverScope

interface MutableSavedStateMap : MutableMap<String, Any?> {
    val saverScope: SaverScope
}
