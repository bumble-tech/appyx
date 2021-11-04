package com.github.zsoltk.composeribs.core

import androidx.annotation.CallSuper
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import com.github.zsoltk.composeribs.core.lifecycle.LifecycleLogger
import com.github.zsoltk.composeribs.core.lifecycle.NodeLifecycle
import com.github.zsoltk.composeribs.core.lifecycle.NodeLifecycleImpl
import com.github.zsoltk.composeribs.core.modality.AncestryInfo
import com.github.zsoltk.composeribs.core.modality.BuildContext
import com.github.zsoltk.composeribs.core.plugin.NodeAware
import com.github.zsoltk.composeribs.core.plugin.Plugin
import com.github.zsoltk.composeribs.core.plugin.Saveable
import com.github.zsoltk.composeribs.core.routing.LocalFallbackUpNavigationHandler
import com.github.zsoltk.composeribs.core.routing.UpHandler
import com.github.zsoltk.composeribs.core.routing.UpNavigationDispatcher

abstract class Node(
    buildContext: BuildContext,
    plugins: List<Plugin> = emptyList()
) : NodeLifecycle {

    private val nodeLifecycle = NodeLifecycleImpl(this)

    val plugins: List<Plugin> = plugins + if (this is Plugin) listOf(this) else emptyList()

    private val parent: Node? =
        when (val ancestryInfo = buildContext.ancestryInfo) {
            is AncestryInfo.Child -> ancestryInfo.anchor
            is AncestryInfo.Root -> null
        }

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val upNavigationDispatcher: UpNavigationDispatcher = UpNavigationDispatcher()

    init {
        lifecycle.addObserver(LifecycleLogger)
        plugins.filterIsInstance<NodeAware>().forEach { it.init(this) }
    }

    @Composable
    fun Compose() {
        CompositionLocalProvider(
            LocalNode provides this,
            LocalLifecycleOwner provides this,
        ) {
            val fallbackUpNavigationDispatcher = LocalFallbackUpNavigationHandler.current
            UpHandler(
                upDispatcher = upNavigationDispatcher,
                nodeUpNavigation = ::performUpNavigation,
                fallbackUpNavigation = { fallbackUpNavigationDispatcher.handle() }
            )
            DerivedSetup()
            Box(modifier = LocalTransitionModifier.current ?: Modifier) {
                // Avoid applying the same transition modifier for the children down in hierarchy
                // in the cases when their parent doesn't provide one
                CompositionLocalProvider(LocalTransitionModifier provides null) {
                    View()
                }
            }
        }
    }

    @Composable
    protected abstract fun View()

    /** Derived classes can declare functional (non-ui) Composable blocks before [View()] is invoked. */
    @Composable
    protected open fun DerivedSetup() {
    }

    override fun getLifecycle(): Lifecycle =
        nodeLifecycle.lifecycle

    override fun updateLifecycleState(state: Lifecycle.State) {
        nodeLifecycle.updateLifecycleState(state)
    }

    @CallSuper
    open fun onSaveInstanceState(scope: SaverScope): SavedStateMap {
        val map = HashMap<String, Any>()
        savePluginsState(scope, map)
        return map
    }

    private fun savePluginsState(
        scope: SaverScope,
        map: MutableMap<String, Any>
    ) {
        val aggregatedPluginState = plugins
            .filterIsInstance<Saveable>()
            .map { saveable -> saveable.onSavedInstanceState(scope) }
            .fold(mutableMapOf<String, Any?>()) { pluginsState, pluginSaved ->
                pluginsState.putAll(pluginSaved)
                pluginsState
            }
        if (aggregatedPluginState.isNotEmpty()) map[KEY_PLUGINS_STATE] = aggregatedPluginState
    }

    fun upNavigation() {
        upNavigationDispatcher.upNavigation()
    }

    protected open fun performUpNavigation(): Boolean =
        parent?.performUpNavigation() == true

    companion object {
        const val KEY_PLUGINS_STATE = "PluginsState"
    }

}
