package com.bumble.appyx.navigation.node

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.core.plugin.Plugin
import com.bumble.appyx.interactions.core.plugin.SavesInstanceState
import com.bumble.appyx.interactions.core.state.MutableSavedStateMap
import com.bumble.appyx.interactions.core.state.MutableSavedStateMapImpl
import com.bumble.appyx.navigation.Appyx
import com.bumble.appyx.navigation.integrationpoint.IntegrationPoint
import com.bumble.appyx.navigation.integrationpoint.IntegrationPointStub
import com.bumble.appyx.navigation.lifecycle.DefaultPlatformLifecycleObserver
import com.bumble.appyx.navigation.lifecycle.Lifecycle
import com.bumble.appyx.navigation.lifecycle.LifecycleLogger
import com.bumble.appyx.navigation.lifecycle.LocalCommonLifecycleOwner
import com.bumble.appyx.navigation.lifecycle.NodeLifecycle
import com.bumble.appyx.navigation.lifecycle.NodeLifecycleImpl
import com.bumble.appyx.navigation.modality.AncestryInfo
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.plugin.Destroyable
import com.bumble.appyx.navigation.plugin.NodeLifecycleAware
import com.bumble.appyx.navigation.plugin.NodeReadyObserver
import com.bumble.appyx.navigation.plugin.UpNavigationHandler
import com.bumble.appyx.navigation.plugin.plugins
import com.bumble.appyx.navigation.state.SavedStateMap
import com.bumble.appyx.navigation.store.RetainedInstanceStore
import com.bumble.appyx.utils.multiplatform.BuildFlags
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

@Suppress("TooManyFunctions")
@Stable
open class Node internal constructor(
    private val buildContext: BuildContext,
    val view: NodeView = EmptyNodeView,
    private val retainedInstanceStore: RetainedInstanceStore,
    plugins: List<Plugin> = emptyList()
) : NodeLifecycle, NodeView by view {

    constructor(
        buildContext: BuildContext,
        view: NodeView = EmptyNodeView,
        plugins: List<Plugin> = emptyList()
    ) : this(buildContext, view, RetainedInstanceStore, plugins)

    @Suppress("LeakingThis") // Implemented in the same way as in androidx.Fragment
    private val nodeLifecycle = NodeLifecycleImpl(this)

    private var wasBuilt = false

    val id: String
        get() = buildContext.identifier

    val plugins: List<Plugin> = plugins + listOfNotNull(this as? Plugin)

    val ancestryInfo: AncestryInfo =
        buildContext.ancestryInfo

    val isRoot: Boolean =
        ancestryInfo == AncestryInfo.Root

    val parent: ParentNode<*>? =
        when (ancestryInfo) {
            is AncestryInfo.Child -> ancestryInfo.anchor
            is AncestryInfo.Root -> null
        }

    override val lifecycle get() = nodeLifecycle.lifecycle

    override val lifecycleScope: CoroutineScope by lazy { lifecycle.coroutineScope }

    var integrationPoint: IntegrationPoint = IntegrationPointStub()
        get() {
            return if (isRoot) field
            else parent?.integrationPoint ?: error(
                "Non-root Node should have a parent"
            )
        }
        set(value) {
            check(isRoot) { "Only a root Node can have an integration point" }
            field = value
        }

    init {
        if (BuildFlags.DEBUG) {
            lifecycle.addObserver(LifecycleLogger(this))
        }
        lifecycle.addObserver(object : DefaultPlatformLifecycleObserver {
            override fun onCreate() {
                if (!wasBuilt) error("onBuilt was not invoked for $this")
            }
        })
    }

    protected suspend inline fun <reified T : Node> executeAction(
        crossinline action: () -> Unit
    ): T = withContext(lifecycleScope.coroutineContext) {
        action()
        this@Node as T
    }

    open fun onBuilt() {
        require(!wasBuilt) { "onBuilt was already invoked" }
        wasBuilt = true
        updateLifecycleState(Lifecycle.State.CREATED)
        plugins<NodeReadyObserver<Node>>().forEach { it.init(this) }
        plugins<NodeLifecycleAware>().forEach { it.onCreate(lifecycle) }
    }

    @Composable
    fun Compose(modifier: Modifier = Modifier) {
        CompositionLocalProvider(
            LocalNode provides this,
            LocalCommonLifecycleOwner provides this,
        ) {
            DerivedSetup()
            View(modifier)
        }
    }

    @Composable
    protected open fun DerivedSetup() {

    }

    override fun updateLifecycleState(state: Lifecycle.State) {
        if (lifecycle.currentState == state) return
        if (lifecycle.currentState == Lifecycle.State.DESTROYED && state != Lifecycle.State.DESTROYED) {
            Appyx.reportException(
                IllegalStateException(
                    "Trying to change lifecycle state of already destroyed node ${this::class}"
                )
            )
            return
        }
        nodeLifecycle.updateLifecycleState(state)
        if (state == Lifecycle.State.DESTROYED) {
            if (!integrationPoint.isChangingConfigurations) {
                retainedInstanceStore.clearStore(id)
            }
            plugins<Destroyable>().forEach { it.destroy() }
        }
    }

    fun saveInstanceState(scope: SaverScope): SavedStateMap {
        val writer = MutableSavedStateMapImpl(saverScope = scope)
        onSaveInstanceState(writer)
        plugins
            .filterIsInstance<SavesInstanceState>()
            .forEach { it.saveInstanceState(writer) }
        return writer.savedState
    }

    protected open fun onSaveInstanceState(state: MutableSavedStateMap) {
        buildContext.onSaveInstanceState(state)
    }

    fun finish() {
        parent?.onChildFinished(this) ?: integrationPoint.onRootFinished()
    }

    /**
     * Triggers parents up navigation (back navigation by default).
     *
     * This method is useful for different cases like:
     * - Close button on the screen which leads back to the previous screen.
     * - Blocker screen that intercepts back button clicks but closes itself when condition is met.
     *
     * To properly handle blocker case this method skips the current node plugins (like router),
     * and invokes the parent directly.
     */
    fun navigateUp() {
        require(parent != null || isRoot) {
            "Can't navigate up, neither parent nor integration point is presented"
        }
        if (parent?.performUpNavigation() != true) {
            integrationPoint.handleUpNavigation()
        }
    }

    open fun performUpNavigation(): Boolean =
        handleUpNavigationByPlugins() || (parent as? Node)?.performUpNavigation() == true

    private fun handleUpNavigationByPlugins(): Boolean =
        plugins<UpNavigationHandler>().any { it.handleUpNavigation() }
}
