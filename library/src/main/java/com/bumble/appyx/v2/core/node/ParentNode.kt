package com.bumble.appyx.v2.core.node

import androidx.activity.compose.BackHandler
import androidx.annotation.CallSuper
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import com.bumble.appyx.v2.core.children.ChildAware
import com.bumble.appyx.v2.core.children.ChildAwareImpl
import com.bumble.appyx.v2.core.children.ChildCallback
import com.bumble.appyx.v2.core.children.ChildEntry
import com.bumble.appyx.v2.core.children.ChildEntryMap
import com.bumble.appyx.v2.core.children.ChildrenCallback
import com.bumble.appyx.v2.core.composable.ChildRenderer
import com.bumble.appyx.v2.core.lifecycle.ChildNodeLifecycleManager
import com.bumble.appyx.v2.core.modality.AncestryInfo
import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.plugin.Plugin
import com.bumble.appyx.v2.core.routing.Resolver
import com.bumble.appyx.v2.core.routing.RoutingKey
import com.bumble.appyx.v2.core.routing.RoutingSource
import com.bumble.appyx.v2.core.routing.source.combined.plus
import com.bumble.appyx.v2.core.routing.source.permanent.PermanentRoutingSource
import com.bumble.appyx.v2.core.routing.source.permanent.operation.add
import com.bumble.appyx.v2.core.state.SavedStateMap
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

abstract class ParentNode<Routing : Any>(
    routingSource: RoutingSource<Routing, *>,
    private val buildContext: BuildContext,
    private val childMode: ChildEntry.ChildMode = ChildEntry.ChildMode.LAZY,
    private val childAware: ChildAware<ParentNode<Routing>> = ChildAwareImpl(),
    plugins: List<Plugin> = listOf(),
) : Node(
    buildContext = buildContext,
    plugins = plugins + routingSource + childAware
), Resolver<Routing>, ChildAware<ParentNode<Routing>> {

    private val permanentRoutingSource = PermanentRoutingSource<Routing>(
        savedStateMap = buildContext.savedStateMap,
        key = KEY_PERMANENT_ROUTING_SOURCE
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

    override val node: ParentNode<Routing>
        get() = this

    private var transitionsInBackgroundJob: Job? = null

    @CallSuper
    override fun onBuilt() {
        super.onBuilt()
        delayedChildRestoration.restoreChildren()?.also { restoredMap ->
            _children.update { restoredMap }
            delayedChildRestoration = null
        }
        lifecycle.coroutineScope.launch { this@ParentNode.routingSource.syncChildrenWithRoutingSource() }
        childNodeLifecycleManager.launch()
        manageTransitions()
    }

    private suspend fun RoutingSource<Routing, *>.syncChildrenWithRoutingSource() {
        elements.collect { elements ->
            _children.update { map ->
                val routingSourceKeys = elements.map { element -> element.key }
                val localKeys = map.keys.toList()
                val newKeys = routingSourceKeys - localKeys
                val removedKeys = localKeys - routingSourceKeys
                val mutableMap = map.toMutableMap()
                newKeys.forEach { key ->
                    mutableMap[key] =
                        ChildEntry.create(key, this@ParentNode, null.toBuildContext(), childMode)
                }
                removedKeys.forEach { key ->
                    mutableMap.remove(key)
                }
                mutableMap
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
    protected fun PermanentChild(
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
                        .filter { it.fromState != it.targetState }
                        .forEach { routingSource.onTransitionFinished(it.key) }
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

    @Composable
    final override fun DerivedSetup() {
        val canHandleBackPress by routingSource.canHandleBackPress.collectAsState()
        BackHandler(canHandleBackPress) {
            routingSource.onBackPressed()
        }
    }

    open fun onChildFinished(child: Node) {
        // TODO warn unhandled child
    }

    @CallSuper
    override fun onSaveInstanceState(scope: SaverScope): SavedStateMap =
        super.onSaveInstanceState(scope) + HashMap<String, Any>().apply {
            saveRoutingState(this)
            saveChildrenState(scope, this)
        }

    private fun saveRoutingState(map: MutableMap<String, Any>) {
        routingSource.saveInstanceState(map)
    }

    private fun saveChildrenState(
        scope: SaverScope,
        map: MutableMap<String, Any>
    ) {
        val children = _children.value
        if (children.isNotEmpty()) {
            val childrenState =
                children
                    .mapValues { (_, entry) ->
                        when (entry) {
                            is ChildEntry.Eager -> entry.node.onSaveInstanceState(scope)
                            is ChildEntry.Lazy -> entry.buildContext.savedStateMap
                        }
                    }
            if (childrenState.isNotEmpty()) map[KEY_CHILDREN_STATE] =
                childrenState
        }
    }

    override fun <T : Node> whenChildAttached(child: KClass<T>, callback: ChildCallback<T>) {
        childAware.whenChildAttached(child, callback)
    }

    override fun <T1 : Node, T2 : Node> whenChildrenAttached(
        child1: KClass<T1>,
        child2: KClass<T2>,
        callback: ChildrenCallback<T1, T2>
    ) {
        childAware.whenChildrenAttached(child1, child2, callback)
    }

    // TODO Investigate how to remove it
    @VisibleForTesting
    internal fun manageTransitionsInTest() {
        manageTransitionsInBackground()
    }

    companion object {
        const val KEY_ROUTING_SOURCE = "RoutingSource"
        const val KEY_CHILDREN_STATE = "ChildrenState"
        private const val KEY_PERMANENT_ROUTING_SOURCE = "KeyPermanentRoutingSource"
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
