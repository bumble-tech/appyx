package com.github.zsoltk.composeribs.core

import androidx.activity.compose.BackHandler
import androidx.annotation.CallSuper
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import com.github.zsoltk.composeribs.core.children.ChildEntry
import com.github.zsoltk.composeribs.core.children.ChildEntryMap
import com.github.zsoltk.composeribs.core.lifecycle.LifecycleLogger
import com.github.zsoltk.composeribs.core.lifecycle.NodeLifecycleManager
import com.github.zsoltk.composeribs.core.routing.Renderable
import com.github.zsoltk.composeribs.core.routing.Resolver
import com.github.zsoltk.composeribs.core.routing.RoutingElement
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.routing.source.backstack.JumpToEndTransitionHandler
import com.github.zsoltk.composeribs.core.routing.transition.TransitionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch

val LocalNode = compositionLocalOf<Node<*>?> { null }

abstract class Node<T>(
    final override val routingSource: RoutingSource<T, *>? = null,
    savedStateMap: SavedStateMap?,
    private val childMode: ChildEntry.ChildMode = ChildEntry.ChildMode.LAZY,
) : Resolver<T>,
    Renderable,
    LifecycleOwner,
    Parent<T> {

    private val _children = MutableStateFlow(savedStateMap?.restoreChildren() ?: emptyMap())
    final override val children: StateFlow<ChildEntryMap<T>> = _children.asStateFlow()

    private val nodeLifecycleManager = NodeLifecycleManager(this)
    private var transitionsInBackgroundJob: Job? = null

    init {
        lifecycle.addObserver(LifecycleLogger)
        routingSource?.let { source ->
            lifecycle.coroutineScope.launch { source.syncChildrenWithRoutingSource() }
        }
        nodeLifecycleManager.observe()
        manageTransitions()
    }

    private suspend fun RoutingSource<T, *>.syncChildrenWithRoutingSource() {
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
                        ChildEntry.create(key, this@Node, null, childMode)
                }
                removedKeys.forEach { key ->
                    mutableMap.remove(key)
                }
                mutableMap
            }
        }
    }

    private fun SavedStateMap.restoreChildren(): ChildEntryMap<T>? =
        (get(KEY_CHILDREN_STATE) as? Map<RoutingKey<T>, SavedStateMap>)?.mapValues {
            ChildEntry.create(it.key, this@Node, it.value, childMode)
        }

    fun childOrCreate(routingKey: RoutingKey<T>): ChildEntry.Eager<T> {
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
                }[routingKey] as ChildEntry.Eager<T>
        }
    }

    @Composable
    fun Compose() {
        CompositionLocalProvider(
            LocalNode provides this,
            LocalLifecycleOwner provides this,
        ) {
            routingSource?.let { source ->
                val canHandleBackPress by source.canHandleBackPress.collectAsState()
                BackHandler(canHandleBackPress) {
                    source.onBackPressed()
                }
            }

            View()
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
        if (transitionsInBackgroundJob != null || routingSource == null) return
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
    fun <S> AnimatedChildNode(
        routingElement: RoutingElement<T, S>?,
        transitionHandler: TransitionHandler<S> = JumpToEndTransitionHandler(),
        decorator: @Composable ChildTransitionScope<S>.(transitionModifier: Modifier, child: @Composable () -> Unit) -> Unit = { modifier, child ->
            Box(modifier = modifier) {
                child()
            }
        }
    ) {
        if (routingElement == null) return
        AnimatedChildNode(
            routingSource = routingSource as RoutingSource<T, S>, // TODO HOW TO FIX?
            routingElement = routingElement,
            childEntry = childOrCreate(routingElement.key),
            transitionHandler = transitionHandler,
            decorator = decorator,
        )
    }

    @CallSuper
    open fun onSaveInstanceState(scope: SaverScope): SavedStateMap {
        val map = mutableMapOf<String, Any>()
        if (routingSource != null) {
            val state = routingSource.saveInstanceState()
            if (state != null) map[KEY_ROUTING_SOURCE] = state
        }
        val children = _children.value
        if (children.isNotEmpty()) {
            val childrenState =
                children
                    .mapValues { (_, entry) ->
                        when (entry) {
                            is ChildEntry.Eager -> entry.node.onSaveInstanceState(scope)
                            is ChildEntry.Lazy -> entry.savedStateMap
                        }
                    }
            if (childrenState.isNotEmpty()) map[KEY_CHILDREN_STATE] = childrenState
        }
        return map
    }

    final override fun getLifecycle(): Lifecycle =
        nodeLifecycleManager.lifecycle

    internal fun updateLifecycleState(state: Lifecycle.State) {
        nodeLifecycleManager.updateLifecycleState(state)
    }

    companion object {
        const val KEY_ROUTING_SOURCE = "RoutingSource"
        const val KEY_CHILDREN_STATE = "ChildrenState"
    }

}
