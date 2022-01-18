package com.github.zsoltk.composeribs.core.plugin

import androidx.compose.runtime.saveable.SaverScope
import androidx.lifecycle.Lifecycle
import com.github.zsoltk.composeribs.core.node.Node
import com.github.zsoltk.composeribs.core.state.SavedStateMap

interface Plugin


inline fun <reified P : Plugin> Node.plugins(): List<P> =
    this.plugins.filterIsInstance(P::class.java)

interface NodeAware<N : Node> : Plugin {
    val node: N

    fun init(node: N) {}
}

interface NodeLifecycleAware : Plugin {
    fun onCreate(lifecycle: Lifecycle) {}
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
