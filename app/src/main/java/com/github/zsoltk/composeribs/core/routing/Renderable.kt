package com.github.zsoltk.composeribs.core.routing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.StateObject

interface Renderable {

    @Composable
    fun View(foo: StateObject)
}
