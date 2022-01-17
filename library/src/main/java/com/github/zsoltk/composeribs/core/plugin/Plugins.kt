package com.github.zsoltk.composeribs.core.plugin

import androidx.compose.runtime.saveable.SaverScope
import androidx.lifecycle.Lifecycle
import com.github.zsoltk.composeribs.core.node.Node
import com.github.zsoltk.composeribs.core.state.SavedStateMap

interface Plugin


inline fun <reified P : Plugin> Node.plugins(): List<P> =
    this.plugins.filterIsInstance(P::class.java)

interface NodeAware : Plugin {
    val node: Node

    fun init(node: Node) {}
}

interface NodeLifecycleAware : Plugin {
    fun onLifecycleUpdated(state: Lifecycle.State) {}
}

interface Saveable : Plugin {
    fun onSavedInstanceState(scope: SaverScope): SavedStateMap
}

interface UpNavigationHandler : Plugin {
    fun handleUpNavigation() = false
}

fun interface Destroyable : Plugin {
    fun destroy()
}

interface BackPressHandler : Plugin {
    fun onBackPressed()
}

/**
 * Bundle for future state restoration.
 * Result should be supported by [androidx.compose.runtime.saveable.SaverScope.canBeSaved].
 */
interface SavesInstanceState : Plugin {
    fun saveInstanceState(savedStateMap: MutableMap<String, Any>) {}
}
