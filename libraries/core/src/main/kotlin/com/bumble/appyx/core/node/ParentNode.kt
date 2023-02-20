package com.bumble.appyx.core.node

import androidx.annotation.CallSuper
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import com.bumble.appyx.Appyx
import com.bumble.appyx.core.children.ChildAware
import com.bumble.appyx.core.children.ChildAwareImpl
import com.bumble.appyx.core.children.ChildCallback
import com.bumble.appyx.core.children.ChildEntry
import com.bumble.appyx.core.children.ChildEntryMap
import com.bumble.appyx.core.children.ChildNodeCreationManager
import com.bumble.appyx.core.children.ChildrenCallback
import com.bumble.appyx.core.children.nodeOrNull
import com.bumble.appyx.core.composable.ChildRenderer
import com.bumble.appyx.core.lifecycle.ChildNodeLifecycleManager
import com.bumble.appyx.core.mapState
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.core.navigation.NavModel
import com.bumble.appyx.core.navigation.Resolver
import com.bumble.appyx.core.navigation.isTransitioning
import com.bumble.appyx.core.navigation.model.combined.plus
import com.bumble.appyx.core.navigation.model.permanent.PermanentNavModel
import com.bumble.appyx.core.navigation.model.permanent.operation.addUnique
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.core.state.MutableSavedStateMap
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlin.reflect.KClass

@Suppress("TooManyFunctions")
@Stable
abstract class ParentNode<NavTarget : Any>(
    navModel: NavModel<NavTarget, *>,
    buildContext: BuildContext,
    view: ParentNodeView<NavTarget> = EmptyParentNodeView(),
    childKeepMode: ChildEntry.KeepMode = Appyx.defaultChildKeepMode,
    private val childAware: ChildAware<ParentNode<NavTarget>> = ChildAwareImpl(),
    plugins: List<Plugin> = listOf(),
) : Node(
    view = view,
    buildContext = buildContext,
    plugins = plugins + navModel + childAware
), Resolver<NavTarget> {

    private val permanentNavModel = PermanentNavModel<NavTarget>(
        savedStateMap = buildContext.savedStateMap,
        key = KEY_PERMANENT_NAV_MODEL,
    )
    val navModel: NavModel<NavTarget, *> = permanentNavModel + navModel

    private val childNodeCreationManager = ChildNodeCreationManager<NavTarget>(
        savedStateMap = buildContext.savedStateMap,
        customisations = buildContext.customisations,
        keepMode = childKeepMode,
    )
    val children: StateFlow<ChildEntryMap<NavTarget>>
        get() = childNodeCreationManager.children

    private val childNodeLifecycleManager = ChildNodeLifecycleManager(
        navModel = this.navModel,
        children = children,
        keepMode = childKeepMode,
        coroutineScope = lifecycleScope,
    )

    private var transitionsInBackgroundJob: Job? = null

    @CallSuper
    override fun onBuilt() {
        super.onBuilt()
        childNodeCreationManager.launch(this)
        childNodeLifecycleManager.launch()
        manageTransitions()
    }

    fun childOrCreate(navKey: NavKey<NavTarget>): ChildEntry.Initialized<NavTarget> =
        childNodeCreationManager.childOrCreate(navKey)

    @Composable
    fun PermanentChild(
        navTarget: NavTarget,
        decorator: @Composable (child: ChildRenderer) -> Unit
    ) {
        LaunchedEffect(navTarget) {
            permanentNavModel.addUnique(navTarget)
        }
        val scope = rememberCoroutineScope()
        val child by remember(navTarget) {
            permanentNavModel
                .elements
                // use WhileSubscribed or Lazy otherwise desynchronisation issue
                .mapState(scope, SharingStarted.WhileSubscribed()) { navElements ->
                    navElements
                        .find { it.key.navTarget == navTarget }
                        ?.let { childOrCreate(it.key) }
                }
        }.collectAsState()
        child?.let {
            decorator(child = PermanentChildRender(it.node))
        }
    }

    @Composable
    fun PermanentChild(navTarget: NavTarget) {
        PermanentChild(navTarget) { child -> child() }
    }

    override fun updateLifecycleState(state: Lifecycle.State) {
        super.updateLifecycleState(state)
        childNodeLifecycleManager.propagateLifecycleToChildren(state)
    }

    /**
     * [NavModel.onTransitionFinished] is invoked by Composable function when animation is finished.
     * When application is in background, recomposition is paused, so we never invoke the callback.
     * Because we do not care about animations in background and still want to have
     * business-logic-driven navigation state change in background, we need to instantly invoke the callback.
     */
    private fun manageTransitions() {
        lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                override fun onStart(owner: LifecycleOwner) {
                    manageTransitionsInForeground()
                }

                override fun onStop(owner: LifecycleOwner) {
                    manageTransitionsInBackground()
                }
            }
        )
    }

    private fun manageTransitionsInBackground() {
        if (transitionsInBackgroundJob == null) {
            transitionsInBackgroundJob = lifecycle.coroutineScope.launch {
                navModel.elements.collect { elements ->
                    elements
                        .mapNotNull { if (it.isTransitioning) it.key else null }
                        .also { navModel.onTransitionFinished(it) }
                }
            }
        }
    }

    private fun manageTransitionsInForeground() {
        transitionsInBackgroundJob?.run {
            cancel()
            transitionsInBackgroundJob = null
        }
    }

    @Deprecated(
        replaceWith = ReplaceWith("attachChild(action"),
        message = "Will be removed in 1.1"
    )
    protected suspend inline fun <reified T : Node> attachWorkflow(
        timeout: Long = ATTACH_WORKFLOW_SYNC_TIMEOUT,
        crossinline action: () -> Unit
    ) = attachChild<T>(timeout, action)

    /**
     * attachChild executes provided action e.g. backstack.push(NodeANavTarget) and waits for the specific
     * Node of type T to appear in the ParentNode's children list. It should happen almost immediately because it happens
     * on the main thread, but the order of actions is not preserved as lifecycleScope uses Dispatchers.Main.immediate.
     * As the result we're doing it asynchronously with timeout after which exception is thrown if
     * expected node has not appeared in the children list.
     */
    protected suspend inline fun <reified T : Node> attachChild(
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
            "Expected child of type [${T::class.java}] was not found after executing action. " +
                    "Check that your action actually results in the expected child."
        }
    }

    /**
     * waitForChildAttached waits for the specific child of type T to be attached. For instance, we may
     * want to wait until user logs in to perform a certain action. Since we don't have control over
     * when this happens this job can hang indefinitely therefore you need to provide timeout if
     * you need one.
     */
    protected suspend inline fun <reified T : Node> waitForChildAttached(): T =
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

    open fun onChildFinished(child: Node) {
        // TODO warn unhandled child
    }

    @CallSuper
    override fun onSaveInstanceState(state: MutableSavedStateMap) {
        super.onSaveInstanceState(state)
        // permanentNavModel is not provided as a plugin, store manually
        permanentNavModel.saveInstanceState(state)
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

    // TODO Investigate how to remove it
    @VisibleForTesting
    internal fun manageTransitionsInTest() {
        manageTransitionsInBackground()
    }

    companion object {
        const val ATTACH_WORKFLOW_SYNC_TIMEOUT = 5000L
        const val KEY_PERMANENT_NAV_MODEL = "PermanentNavModel"
    }

    private class PermanentChildRender(private val node: Node) : ChildRenderer {

        @Suppress(
            "ComposableNaming" // This wants to be 'Invoke' but that won't work with 'operator'.
        )
        @Composable
        override operator fun invoke(modifier: Modifier) {
            node.Compose(modifier)
        }

        @Suppress(
            "ComposableNaming" // This wants to be 'Invoke' but that won't work with 'operator'.
        )
        @Composable
        override operator fun invoke() {
            invoke(modifier = Modifier)
        }
    }

}
