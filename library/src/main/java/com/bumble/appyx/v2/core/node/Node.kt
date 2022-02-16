package com.bumble.appyx.v2.core.node

import androidx.annotation.CallSuper
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.bumble.appyx.v2.core.composable.LocalTransitionModifier
import com.bumble.appyx.v2.core.integrationpoint.IntegrationPoint
import com.bumble.appyx.v2.core.integrationpoint.IntegrationPointStub
import com.bumble.appyx.v2.core.lifecycle.LifecycleLogger
import com.bumble.appyx.v2.core.lifecycle.NodeLifecycle
import com.bumble.appyx.v2.core.lifecycle.NodeLifecycleImpl
import com.bumble.appyx.v2.core.modality.AncestryInfo
import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.plugin.Destroyable
import com.bumble.appyx.v2.core.plugin.NodeAware
import com.bumble.appyx.v2.core.plugin.NodeLifecycleAware
import com.bumble.appyx.v2.core.plugin.Plugin
import com.bumble.appyx.v2.core.plugin.Saveable
import com.bumble.appyx.v2.core.plugin.UpNavigationHandler
import com.bumble.appyx.v2.core.plugin.plugins
import com.bumble.appyx.v2.core.routing.upnavigation.FallbackUpNavigationHandler
import com.bumble.appyx.v2.core.routing.upnavigation.LocalFallbackUpNavigationHandler
import com.bumble.appyx.v2.core.routing.upnavigation.UpHandler
import com.bumble.appyx.v2.core.routing.upnavigation.UpNavigationDispatcher
import com.bumble.appyx.v2.core.state.SavedStateMap

abstract class Node(
    buildContext: BuildContext,
    plugins: List<Plugin> = emptyList()
) : NodeLifecycle {

    private val nodeLifecycle = NodeLifecycleImpl(this)

    val plugins: List<Plugin> = plugins + if (this is Plugin) listOf(this) else emptyList()

    val ancestryInfo: AncestryInfo =
        buildContext.ancestryInfo

    val isRoot: Boolean =
        ancestryInfo == AncestryInfo.Root

    private val parent: ParentNode<*>? =
        when (ancestryInfo) {
            is AncestryInfo.Child -> ancestryInfo.anchor
            is AncestryInfo.Root -> null
        }

    var integrationPoint: IntegrationPoint = IntegrationPointStub()
        get() {
            return if (isRoot) field
            else parent?.integrationPoint ?: error(
                "Non-root Node should have a parent"
            )
        }

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val upNavigationDispatcher: UpNavigationDispatcher =
        UpNavigationDispatcher(::performUpNavigation)

    private var wasBuilt = false

    init {
        lifecycle.addObserver(LifecycleLogger)
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                if (!wasBuilt) error("onBuilt was not invoked for $this")
            }
        });
    }

    @CallSuper
    open fun onBuilt() {
        require(!wasBuilt) { "onBuilt was already invoked" }
        wasBuilt = true
        updateLifecycleState(Lifecycle.State.CREATED)
        plugins<NodeAware<Node>>().forEach { it.init(this) }
        plugins<NodeLifecycleAware>().forEach { it.onCreate(lifecycle) }
    }

    @Composable
    fun Compose(modifier: Modifier = Modifier) {
        CompositionLocalProvider(
            LocalNode provides this,
            LocalLifecycleOwner provides this,
        ) {
            val fallbackUpNavigationDispatcher = LocalFallbackUpNavigationHandler.current
            UpHandler(
                upDispatcher = upNavigationDispatcher,
                fallbackUpNavigation = fallbackUpNavigationDispatcher,
            )
            DerivedSetup()
            Box(modifier = LocalTransitionModifier.current ?: Modifier) {
                // Avoid applying the same transition modifier for the children down in hierarchy
                // in the cases when their parent doesn't provide one
                CompositionLocalProvider(LocalTransitionModifier provides null) {
                    View(modifier)
                }
            }
        }
    }

    /** Derived classes can declare functional (non-ui) Composable blocks before [View()] is invoked. */
    @Composable
    protected open fun DerivedSetup() {
    }

    @Composable
    protected abstract fun View(modifier: Modifier)

    override fun getLifecycle(): Lifecycle =
        nodeLifecycle.lifecycle

    override fun updateLifecycleState(state: Lifecycle.State) {
        nodeLifecycle.updateLifecycleState(state)
        if (state == Lifecycle.State.DESTROYED) {
            plugins<Destroyable>().forEach { it.destroy() }
        }
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

    fun finish() {
        parent?.onChildFinished(this) ?: integrationPoint.onRootFinished()
    }

    fun navigateUp() {
        upNavigationDispatcher.upNavigation()
    }

    private fun handleUpNavigationByPlugins(): Boolean =
        plugins<UpNavigationHandler>().any { it.handleUpNavigation() }

    protected open fun performUpNavigation(): Boolean =
        handleUpNavigationByPlugins() || parent?.performUpNavigation() == true

    // TODO Investigate how to remove it
    @VisibleForTesting
    internal fun injectFallbackUpNavigationHandler(handler: FallbackUpNavigationHandler) {
        upNavigationDispatcher.setFallbackUpNavigationCallback(handler)
    }

    companion object {
        const val KEY_PLUGINS_STATE = "PluginsState"
    }

}
