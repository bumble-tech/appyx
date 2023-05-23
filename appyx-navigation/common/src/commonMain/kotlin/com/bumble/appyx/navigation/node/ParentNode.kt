package com.bumble.appyx.navigation.node

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.interactions.core.model.InteractionModel
import com.bumble.appyx.interactions.core.model.plus
import com.bumble.appyx.interactions.core.plugin.Plugin
import com.bumble.appyx.interactions.core.state.MutableSavedStateMap
import com.bumble.appyx.interactions.core.ui.helper.InteractionModelSetup
import com.bumble.appyx.interactions.permanent.PermanentInteractionModel
import com.bumble.appyx.interactions.permanent.PermanentModel
import com.bumble.appyx.interactions.permanent.operation.addUnique
import com.bumble.appyx.navigation.Appyx
import com.bumble.appyx.navigation.children.ChildAware
import com.bumble.appyx.navigation.children.ChildAwareImpl
import com.bumble.appyx.navigation.children.ChildCallback
import com.bumble.appyx.navigation.children.ChildEntry
import com.bumble.appyx.navigation.children.ChildEntryMap
import com.bumble.appyx.navigation.children.ChildNodeCreationManager
import com.bumble.appyx.navigation.children.ChildrenCallback
import com.bumble.appyx.navigation.children.nodeOrNull
import com.bumble.appyx.navigation.composable.ChildRenderer
import com.bumble.appyx.navigation.lifecycle.ChildNodeLifecycleManager
import com.bumble.appyx.navigation.mapState
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.navigation.Resolver
import com.bumble.appyx.navigation.platform.PlatformLifecycle
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
abstract class ParentNode<InteractionTarget : Any>(
    interactionModel: InteractionModel<InteractionTarget, *>,
    buildContext: BuildContext,
    view: ParentNodeView<InteractionTarget> = EmptyParentNodeView(),
    childKeepMode: ChildEntry.KeepMode = Appyx.defaultChildKeepMode,
    private val childAware: ChildAware<ParentNode<InteractionTarget>> = ChildAwareImpl(),
    plugins: List<Plugin> = listOf(),
) : Node(
    view = view,
    buildContext = buildContext,
    plugins = plugins + interactionModel + childAware
), Resolver<InteractionTarget> {

    private val permanentInteractionModel = PermanentInteractionModel<InteractionTarget>(
        model = PermanentModel(buildContext.savedStateMap)
    )
    val interactionModel: InteractionModel<InteractionTarget, *> =
        interactionModel + permanentInteractionModel

    private val childNodeCreationManager = ChildNodeCreationManager<InteractionTarget>(
        savedStateMap = buildContext.savedStateMap,
        customisations = buildContext.customisations,
        keepMode = childKeepMode,
    )
    val children: StateFlow<ChildEntryMap<InteractionTarget>>
        get() = childNodeCreationManager.children

    private val childNodeLifecycleManager = ChildNodeLifecycleManager(
        interactionModel = this.interactionModel,
        children = children,
        keepMode = childKeepMode,
        coroutineScope = lifecycleScope,
    )

    override fun onBuilt() {
        super.onBuilt()
        childNodeCreationManager.launch(this)
        childNodeLifecycleManager.launch()
    }

    fun childOrCreate(element: Element<InteractionTarget>): ChildEntry.Initialized<InteractionTarget> =
        childNodeCreationManager.childOrCreate(element)

    @Composable
    fun PermanentChild(
        interactionTarget: InteractionTarget,
        decorator: @Composable (child: ChildRenderer) -> Unit
    ) {

        LaunchedEffect(interactionTarget) {
            permanentInteractionModel.addUnique(interactionTarget)
        }
        val scope = rememberCoroutineScope()
        val child by remember(interactionTarget, this) {
            children
                .mapState(scope, SharingStarted.WhileSubscribed()) { childrenMap ->
                    childrenMap
                        .keys
                        .find { it.interactionTarget == interactionTarget }
                        ?.let { childOrCreate(it) }
                }
        }.collectAsState()


        child?.let {
            decorator(PermanentChildRender(it.node))
        }
    }

    @Composable
    fun PermanentChild(interactionTarget: InteractionTarget) {
        PermanentChild(interactionTarget) { child -> child() }
    }

    override fun updateLifecycleState(state: PlatformLifecycle.State) {
        super.updateLifecycleState(state)
        childNodeLifecycleManager.propagateLifecycleToChildren(state)

        // TODO move to plugins
        if (state == PlatformLifecycle.State.DESTROYED) {
            interactionModel.destroy()
        }
    }

    @Composable
    override fun DerivedSetup() {
        InteractionModelSetup(interactionModel = interactionModel)
        BackHandler()
    }

    @Composable
    private fun BackHandler() {
        //todo support delegating to plugins
        val canHandleBack = interactionModel
            .canHandeBackPress()
            .collectAsState(initial = false)
        BackHandler(canHandleBack.value) {
            interactionModel.handleBackPress()
        }
    }

    override fun performUpNavigation(): Boolean =
        interactionModel.handleBackPress() || super.performUpNavigation()

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

    // TODO save/restore state properly
    override fun onSaveInstanceState(state: MutableSavedStateMap) {
        super.onSaveInstanceState(state)
        // permanentNavModel is not provided as a plugin, store manually
//        permanentNavModel.saveInstanceState(state)
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
