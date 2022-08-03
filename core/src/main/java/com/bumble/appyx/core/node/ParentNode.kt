package com.bumble.appyx.core.node

import androidx.annotation.CallSuper
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import com.bumble.appyx.core.children.ChildAware
import com.bumble.appyx.core.children.ChildAwareImpl
import com.bumble.appyx.core.children.ChildCallback
import com.bumble.appyx.core.children.ChildEntry
import com.bumble.appyx.core.children.ChildEntryMap
import com.bumble.appyx.core.children.ChildrenCallback
import com.bumble.appyx.core.composable.ChildRenderer
import com.bumble.appyx.core.lifecycle.ChildNodeLifecycleManager
import com.bumble.appyx.core.modality.AncestryInfo
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.core.routing.Resolver
import com.bumble.appyx.core.routing.RoutingKey
import com.bumble.appyx.core.routing.RoutingSource
import com.bumble.appyx.core.routing.isTransitioning
import com.bumble.appyx.core.routing.source.combined.plus
import com.bumble.appyx.core.routing.source.permanent.PermanentRoutingSource
import com.bumble.appyx.core.routing.source.permanent.operation.add
import com.bumble.appyx.core.state.MutableSavedStateMap
import com.bumble.appyx.core.state.SavedStateMap
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

abstract class ParentNode<Routing : Any>(
    routingSource: RoutingSource<Routing, *>,
    private val buildContext: BuildContext,
    view: ParentNodeView<Routing> = EmptyParentNodeView(),
    private val childMode: ChildEntry.ChildMode = ChildEntry.ChildMode.EAGER,
    private val childAware: ChildAware<ParentNode<Routing>> = ChildAwareImpl(),
    plugins: List<Plugin> = listOf(),
) : Node(
    view = view,
    buildContext = buildContext,
    plugins = plugins + routingSource + childAware + view
), Resolver<Routing> {

    private val permanentRoutingSource = PermanentRoutingSource<Routing>(
        savedStateMap = buildContext.savedStateMap,
        key = KEY_PERMANENT_ROUTING_SOURCE,
    )
    val routingSource: RoutingSource<Routing, *> = permanentRoutingSource + routingSource

    // It is impossible to restore _children directly because information for resolver is not ready yet
    private var delayedChildRestoration: SavedStateMap? = buildContext.savedStateMap
    private val _children =
        MutableStateFlow<Map<RoutingKey<Routing>, ChildEntry<Routing>>>(emptyMap())
    val children: StateFlow<ChildEntryMap<Routing>> = _children.asStateFlow()

    private val childNodeLifecycleManager = ChildNodeLifecycleManager(
        lifecycle = lifecycle,
        routingSource = this.routingSource,
        children = children,
    )

    private var transitionsInBackgroundJob: Job? = null

    @CallSuper
    override fun onBuilt() {
        super.onBuilt()
        delayedChildRestoration.restoreChildren()?.also { restoredMap ->
            _children.update { restoredMap }
            delayedChildRestoration = null
        }
        syncChildrenWithRoutingSource()
        childNodeLifecycleManager.launch()
        manageTransitions()
    }

    private fun syncChildrenWithRoutingSource() {
        lifecycle.coroutineScope.launch {
            routingSource.elements.collect { elements ->
                _children.update { map ->
                    val routingSourceKeys = elements
                        .mapTo(HashSet(elements.size, 1f)) { element -> element.key }
                    val localKeys = map.keys
                    val newKeys = routingSourceKeys - localKeys
                    val removedKeys = localKeys - routingSourceKeys
                    val mutableMap = map.toMutableMap()
                    newKeys.forEach { key ->
                        mutableMap[key] =
                            ChildEntry.create(
                                key = key,
                                resolver = this@ParentNode,
                                buildContext = null.toBuildContext(),
                                childMode = childMode,
                            )
                    }
                    removedKeys.forEach { key ->
                        mutableMap.remove(key)
                    }
                    mutableMap
                }
            }
        }
    }

    private fun SavedStateMap?.restoreChildren(): ChildEntryMap<Routing>? =
        (this?.get(KEY_CHILDREN_STATE) as? Map<RoutingKey<Routing>, SavedStateMap>)?.mapValues {
            ChildEntry.create(it.key, this@ParentNode, it.value.toBuildContext(), childMode)
        }

    private fun SavedStateMap?.toBuildContext(): BuildContext =
        BuildContext(
            ancestryInfo = AncestryInfo.Child(this@ParentNode),
            savedStateMap = this,
            customisations = buildContext.customisations.getSubDirectoryOrSelf(
                this@ParentNode::class
            )
        )

    fun childOrCreate(routingKey: RoutingKey<Routing>): ChildEntry.Eager<Routing> {
        val value = _children.value
        val child = value[routingKey]
            ?: error("Rendering and children management is out of sync: requested $routingKey but have only ${value.keys}")
        return when (child) {
            is ChildEntry.Eager ->
                child
            is ChildEntry.Lazy ->
                _children.updateAndGet { map ->
                    val updateChild = map[routingKey]
                        ?: error("Requested child $routingKey disappeared")
                    when (updateChild) {
                        is ChildEntry.Eager -> map
                        is ChildEntry.Lazy -> map.plus(routingKey to updateChild.initialize())
                    }
                }[routingKey] as ChildEntry.Eager
        }
    }

    @Composable
    fun PermanentChild(
        routing: Routing,
        decorator: @Composable (child: ChildRenderer) -> Unit
    ) {
        var child by remember { mutableStateOf<ChildEntry.Eager<*>?>(null) }
        LaunchedEffect(routing) {
            permanentRoutingSource.elements.collect { elements ->
                val routingKey = elements.find { it.key.routing == routing }?.key
                    ?: RoutingKey(routing).also { permanentRoutingSource.add(it) }
                child = childOrCreate(routingKey)
            }
        }

        child?.let {
            decorator(child = PermanentChildRender(it.node))
        }
    }

    @Composable
    fun PermanentChild(routing: Routing) {
        PermanentChild(routing) { child -> child() }
    }

    /**
     * [RoutingSource.onTransitionFinished] is invoked by Composable function when animation is finished.
     * When application is in background, recomposition is paused, so we never invoke the callback.
     * Because we do not care about animations in background and still want to have
     * business-logic-driven routing in background, we need to instantly invoke the callback.
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
                routingSource.elements.collect { elements ->
                    elements
                        .mapNotNull { if (it.isTransitioning) it.key else null }
                        .also { routingSource.onTransitionFinished(it) }
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

    open fun onChildFinished(child: Node) {
        // TODO warn unhandled child
    }

    @CallSuper
    override fun onSaveInstanceState(state: MutableSavedStateMap) {
        super.onSaveInstanceState(state)
        // permanentRoutingSource is not provided as a plugin, store manually
        permanentRoutingSource.saveInstanceState(state)
        saveChildrenState(state)
    }

    private fun saveChildrenState(writer: MutableSavedStateMap) {
        val children = _children.value
        if (children.isNotEmpty()) {
            val childrenState =
                children
                    .mapValues { (_, entry) ->
                        when (entry) {
                            is ChildEntry.Eager -> entry.node.saveInstanceState(writer.saverScope)
                            is ChildEntry.Lazy -> entry.buildContext.savedStateMap
                        }
                    }
            if (childrenState.isNotEmpty()) {
                writer[KEY_CHILDREN_STATE] = childrenState
            }
        }
    }

    // region ChildAware

    protected fun <T : Node> whenChildAttached(child: KClass<T>, callback: ChildCallback<T>) {
        childAware.whenChildAttached(child, callback)
    }

    protected fun <T1 : Node, T2 : Node> whenChildrenAttached(
        child1: KClass<T1>,
        child2: KClass<T2>,
        callback: ChildrenCallback<T1, T2>
    ) {
        childAware.whenChildrenAttached(child1, child2, callback)
    }

    protected inline fun <reified T : Node> whenChildAttached(
        noinline callback: ChildCallback<T>,
    ) {
        whenChildAttached(T::class, callback)
    }

    protected inline fun <reified T1 : Node, reified T2 : Node> whenChildrenAttached(
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
        const val KEY_CHILDREN_STATE = "ChildrenState"
        const val KEY_PERMANENT_ROUTING_SOURCE = "PermanentRoutingSource"
    }

    private class PermanentChildRender(private val node: Node) : ChildRenderer {

        @Composable
        override fun invoke(modifier: Modifier) {
            node.Compose(modifier)
        }

        @Composable
        override fun invoke() {
            invoke(modifier = Modifier)
        }
    }

}
