package com.github.zsoltk.composeribs.core.plugin

import androidx.compose.runtime.saveable.SaverScope
import com.github.zsoltk.composeribs.core.node.Node
import com.github.zsoltk.composeribs.core.state.SavedStateMap

interface Plugin


inline fun <reified P : Plugin> Node.plugins(): List<P> =
    this.plugins.filterIsInstance(P::class.java)

interface NodeAware : Plugin {
    val node: Node

    fun init(node: Node) {}
}

interface Saveable : Plugin {
    fun onSavedInstanceState(scope: SaverScope): SavedStateMap
}

interface UpNavigationHandler : Plugin {
    fun handleUpNavigation(): Boolean
}

fun interface Destroyable : Plugin {
    fun destroy()
}
