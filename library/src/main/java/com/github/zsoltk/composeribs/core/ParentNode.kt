package com.github.zsoltk.composeribs.core

import androidx.activity.compose.BackHandler
import androidx.annotation.CallSuper
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.SaverScope
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import com.github.zsoltk.composeribs.core.children.ChildAware
import com.github.zsoltk.composeribs.core.children.ChildAwareImpl
import com.github.zsoltk.composeribs.core.children.ChildCallback
import com.github.zsoltk.composeribs.core.children.ChildEntry
import com.github.zsoltk.composeribs.core.children.ChildEntryMap
import com.github.zsoltk.composeribs.core.children.ChildrenCallback
import com.github.zsoltk.composeribs.core.lifecycle.ChildNodeLifecycleManager
import com.github.zsoltk.composeribs.core.modality.AncestryInfo
import com.github.zsoltk.composeribs.core.modality.BuildContext
import com.github.zsoltk.composeribs.core.plugin.Plugin
import com.github.zsoltk.composeribs.core.plugin.UpNavigationHandler
import com.github.zsoltk.composeribs.core.routing.Resolver
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

abstract class ParentNode<Routing>(
    val routingSource: RoutingSource<Routing, *>,
    buildContext: BuildContext,
    private val childMode: ChildEntry.ChildMode = ChildEntry.ChildMode.LAZY,
    plugins: List<Plugin> = emptyList(),
) : Node(buildContext = buildContext, plugins = plugins + routingSource),
    Resolver<Routing>,
    ChildAware {

    private val _children =
        MutableStateFlow(buildContext.savedStateMap?.restoreChildren() ?: emptyMap())
    val children: StateFlow<ChildEntryMap<Routing>> = _children.asStateFlow()

    private val childNodeLifecycleManager = ChildNodeLifecycleManager(
        lifecycle = lifecycle,
        routingSource = routingSource,
        children = children,
    )
    private val childAware = ChildAwareImpl(
        children = children,
        lifecycle = lifecycle,
    )

    private var transitionsInBackgroundJob: Job? = null

    init {
        lifecycle.coroutineScope.launch { routingSource.syncChildrenWithRoutingSource() }
        manageTransitions()
    }

    private suspend fun RoutingSource<Routing, *>.syncChildrenWithRoutingSource() {
        all.collect { elements ->
            _children.update { map ->
                val routingSourceKeys =
                    elements.mapTo(HashSet(elements.size)) { element -> element.key }
                val localKeys = map.keys
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

    private fun SavedStateMap.restoreChildren(): ChildEntryMap<Routing>? =
        (get(KEY_CHILDREN_STATE) as? Map<RoutingKey<Routing>, SavedStateMap>)?.mapValues {
            ChildEntry.create(it.key, this@ParentNode, it.value.toBuildContext(), childMode)
        }

    private fun SavedStateMap?.toBuildContext(): BuildContext =
        BuildContext(
            ancestryInfo = AncestryInfo.Child(this@ParentNode),
            savedStateMap = this
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
        if (transitionsInBackgroundJob != null) return
        transitionsInBackgroundJob = lifecycle.coroutineScope.launch {
            routingSource.all.collect { elements ->
                elements
                    .filter { it.fromState != it.targetState }
                    .forEach { routingSource.onTransitionFinished(it.key) }
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

    @CallSuper
    override fun onSaveInstanceState(scope: SaverScope): SavedStateMap =
        super.onSaveInstanceState(scope) + HashMap<String, Any>().apply {
            saveRoutingState(this)
            saveChildrenState(scope, this)
        }

    private fun saveRoutingState(map: MutableMap<String, Any>) {
        val state = routingSource.saveInstanceState()
        if (state != null) map[KEY_ROUTING_SOURCE] = state
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

    override fun performUpNavigation(): Boolean =
        handleSubtreeUpNavigation() || super.performUpNavigation()

    private fun handleSubtreeUpNavigation(): Boolean =
        plugins.filterIsInstance<UpNavigationHandler>().any { it.handleUpNavigation() }

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

    @VisibleForTesting
    internal fun manageTransitionsInTest() {
        manageTransitionsInBackground()
    }

    companion object {
        const val KEY_ROUTING_SOURCE = "RoutingSource"
        const val KEY_CHILDREN_STATE = "ChildrenState"
    }

}
