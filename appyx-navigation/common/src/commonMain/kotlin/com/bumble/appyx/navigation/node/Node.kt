package com.bumble.appyx.navigation.node

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.model.AppyxComponent
import com.bumble.appyx.interactions.model.Element
import com.bumble.appyx.interactions.plugin.Plugin
import com.bumble.appyx.interactions.plugin.SavesInstanceState
import com.bumble.appyx.interactions.state.MutableSavedStateMap
import com.bumble.appyx.interactions.state.MutableSavedStateMapImpl
import com.bumble.appyx.interactions.ui.helper.AppyxComponentSetup
import com.bumble.appyx.navigation.Appyx
import com.bumble.appyx.navigation.children.ChildAware
import com.bumble.appyx.navigation.children.ChildAwareImpl
import com.bumble.appyx.navigation.children.ChildCallback
import com.bumble.appyx.navigation.children.ChildEntry
import com.bumble.appyx.navigation.children.ChildEntryMap
import com.bumble.appyx.navigation.children.ChildNodeCreationManager
import com.bumble.appyx.navigation.children.ChildrenCallback
import com.bumble.appyx.navigation.children.nodeOrNull
import com.bumble.appyx.navigation.lifecycle.ChildNodeLifecycleManager
import com.bumble.appyx.navigation.lifecycle.Lifecycle
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.children.ChildNodeBuilder
import com.bumble.appyx.navigation.integration.IntegrationPoint
import com.bumble.appyx.navigation.integration.IntegrationPointStub
import com.bumble.appyx.navigation.lifecycle.DefaultPlatformLifecycleObserver
import com.bumble.appyx.navigation.lifecycle.LifecycleLogger
import com.bumble.appyx.navigation.lifecycle.LocalCommonLifecycleOwner
import com.bumble.appyx.navigation.lifecycle.NodeLifecycle
import com.bumble.appyx.navigation.lifecycle.NodeLifecycleImpl
import com.bumble.appyx.navigation.modality.AncestryInfo
import com.bumble.appyx.navigation.platform.PlatformBackHandler
import com.bumble.appyx.navigation.plugin.*
import com.bumble.appyx.navigation.store.RetainedInstanceStore
import com.bumble.appyx.utils.multiplatform.BuildFlags
import com.bumble.appyx.utils.multiplatform.SavedStateMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlin.reflect.KClass

@Suppress("TooManyFunctions")
@Stable
abstract class Node<NavTarget : Any>(
    val appyxComponent: AppyxComponent<NavTarget, *>,
    private val nodeContext: NodeContext,
    view: NodeView = EmptyNodeView(),
    childKeepMode: ChildEntry.KeepMode = Appyx.defaultChildKeepMode,
    private val childAware: ChildAware<Node<NavTarget>> = ChildAwareImpl(),
    private val retainedInstanceStore: RetainedInstanceStore,
    plugins: List<Plugin> = listOf(),
) : NodeLifecycle,
    NodeView by view,
    ChildNodeBuilder<NavTarget> {

    constructor(
        appyxComponent: AppyxComponent<NavTarget, *>,
        nodeContext: NodeContext,
        view: NodeView = EmptyNodeView(),
        childKeepMode: ChildEntry.KeepMode = Appyx.defaultChildKeepMode,
        childAware: ChildAware<Node<NavTarget>> = ChildAwareImpl(),
        plugins: List<Plugin> = emptyList()
    ) : this(
        appyxComponent,
        nodeContext,
        view,
        childKeepMode,
        childAware,
        RetainedInstanceStore,
        plugins
    )

    @Suppress("LeakingThis") // Implemented in the same way as in androidx.Fragment
    private val nodeLifecycle = NodeLifecycleImpl(this)

    private var wasBuilt = false

    val id: String
        get() = nodeContext.identifier

    val plugins: List<Plugin> = plugins + listOfNotNull(this as? Plugin)

    val ancestryInfo: AncestryInfo =
        nodeContext.ancestryInfo

    val isRoot: Boolean =
        ancestryInfo == AncestryInfo.Root

    val parent: Node<*>? =
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

    private val childNodeCreationManager = ChildNodeCreationManager<NavTarget>(
        savedStateMap = nodeContext.savedStateMap,
        customisations = nodeContext.customisations,
        keepMode = childKeepMode,
    )
    val children: StateFlow<ChildEntryMap<NavTarget>>
        get() = childNodeCreationManager.children

    private val childNodeLifecycleManager = ChildNodeLifecycleManager(
        appyxComponent = this.appyxComponent,
        children = children,
        keepMode = childKeepMode,
        coroutineScope = lifecycleScope,
    )

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

    open fun onBuilt() {
        require(!wasBuilt) { "onBuilt was already invoked" }
        wasBuilt = true
        updateLifecycleState(Lifecycle.State.CREATED)
        plugins<NodeReadyObserver<Node<*>>>().forEach { it.init(this) }
        plugins<NodeLifecycleAware>().forEach { it.onCreate(lifecycle) }
        plugins<Tooling>().forEach { it.onSetupTooling(lifecycle) }
        childNodeCreationManager.launch(this)
        childNodeLifecycleManager.launch()
    }

    fun childOrCreate(element: Element<NavTarget>): ChildEntry.Initialized<NavTarget> =
        childNodeCreationManager.childOrCreate(element)

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
        childNodeLifecycleManager.propagateLifecycleToChildren(state)

        // TODO move to plugins
        if (state == Lifecycle.State.DESTROYED) {
            appyxComponent.destroy()
        }
    }

    @Composable
    fun Compose(modifier: Modifier = Modifier) {
        CompositionLocalProvider(
            LocalNode provides this,
            LocalCommonLifecycleOwner provides this,
        ) {
            DerivedSetup()
            Content(modifier)
        }
    }

    @Composable
    private fun DerivedSetup() {
        AppyxComponentSetup(appyxComponent = appyxComponent)
        BackHandler()
    }

    @Composable
    private fun BackHandler() {
        //todo support delegating to plugins
        val canHandleBack = appyxComponent
            .canHandleBackPress()
            .collectAsState(initial = false)
        PlatformBackHandler(enabled = canHandleBack.value) {
            appyxComponent.handleBackPress()
        }
    }

    fun performUpNavigation(): Boolean =
        appyxComponent.handleBackPress() ||
            handleUpNavigationByPlugins() ||
            parent?.performUpNavigation() == true

    private fun handleUpNavigationByPlugins(): Boolean =
        plugins<UpNavigationHandler>().any { it.handleUpNavigation() }


    protected suspend inline fun <reified T : Node<*>> executeAction(
        crossinline action: suspend () -> Unit
    ): T = withContext(lifecycleScope.coroutineContext) {
        action()
        this@Node as T
    }

    /**
     * attachChild executes provided action e.g. backstack.push(NodeANavTarget) and waits for the specific
     * Node of type T to appear in the ParentNode's children list. It should happen almost immediately because it happens
     * on the main thread, but the order of actions is not preserved as lifecycleScope uses Dispatchers.Main.immediate.
     * As the result we're doing it asynchronously with timeout after which exception is thrown if
     * expected node has not appeared in the children list.
     */
    protected suspend inline fun <reified T : Node<*>> attachChild(
        timeout: Long = ATTACH_WORKFLOW_SYNC_TIMEOUT,
        crossinline action: () -> Unit
    ): T = withContext(lifecycleScope.coroutineContext) {
        action()

        // after executing action waiting for the children to sync with navModel and
        // throw an exception after short timeout if desired child was not found
        val result = withTimeoutOrNull(timeout) {
            waitForChildAttached<T>()
        }
        checkNotNull(result) {
            "Expected child of type [${T::class}] was not found after executing action. " +
                    "Check that your action actually results in the expected child."
        }
    }

    /**
     * waitForChildAttached waits for the specific child of type T to be attached. For instance, we may
     * want to wait until user logs in to perform a certain action. Since we don't have control over
     * when this happens this job can hang indefinitely therefore you need to provide timeout if
     * you need one.
     */
    protected suspend inline fun <reified T : Node<*>> waitForChildAttached(): T =
        suspendCancellableCoroutine { continuation ->
            lifecycleScope.launch {
                children.collect { childMap ->
                    val childNodeOfExpectedType = childMap.entries
                        .mapNotNull { it.value.nodeOrNull }
                        .filterIsInstance<T>()
                        .takeIf { it.isNotEmpty() }
                        ?.last()

                    if (childNodeOfExpectedType != null && !continuation.isCompleted) {
                        continuation.resume(childNodeOfExpectedType)
                    }
                }
            }.invokeOnCompletion {
                continuation.cancel()
            }
        }

    open fun onChildFinished(child: Node<*>) {
        // TODO warn unhandled child
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

    fun saveInstanceState(scope: SaverScope): SavedStateMap {
        val writer = MutableSavedStateMapImpl(saverScope = scope)
        onSaveInstanceState(writer)
        plugins
            .filterIsInstance<SavesInstanceState>()
            .forEach { it.saveInstanceState(writer) }
        return writer.savedState
    }

    fun finish() {
        parent?.onChildFinished(this) ?: integrationPoint.onRootFinished()
    }

    // TODO save/restore state properly
    fun onSaveInstanceState(state: MutableSavedStateMap) {
        nodeContext.onSaveInstanceState(state)
        childNodeCreationManager.saveChildrenState(state)
    }

    // region ChildAware

    protected fun <T : Any> whenChildAttached(child: KClass<T>, callback: ChildCallback<T>) {
        childAware.whenChildAttached(child, callback)
    }

    protected fun <T1 : Any, T2 : Any> whenChildrenAttached(
        child1: KClass<T1>,
        child2: KClass<T2>,
        callback: ChildrenCallback<T1, T2>
    ) {
        childAware.whenChildrenAttached(child1, child2, callback)
    }

    protected inline fun <reified T : Any> whenChildAttached(
        noinline callback: ChildCallback<T>,
    ) {
        whenChildAttached(T::class, callback)
    }

    protected inline fun <reified T1 : Any, reified T2 : Any> whenChildrenAttached(
        noinline callback: ChildrenCallback<T1, T2>,
    ) {
        whenChildrenAttached(T1::class, T2::class, callback)
    }

    // endregion

    companion object {
        const val ATTACH_WORKFLOW_SYNC_TIMEOUT = 5000L
    }

}
