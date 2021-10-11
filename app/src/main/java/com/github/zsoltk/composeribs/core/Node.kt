package com.github.zsoltk.composeribs.core

import android.os.Parcelable
import androidx.activity.compose.BackHandler
import androidx.annotation.CallSuper
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.github.zsoltk.composeribs.core.routing.Renderable
import com.github.zsoltk.composeribs.core.routing.Resolver
import com.github.zsoltk.composeribs.core.routing.RoutingElement
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.routing.source.backstack.JumpToEndTransitionHandler
import com.github.zsoltk.composeribs.core.routing.transition.TransitionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch

val LocalNode = compositionLocalOf<Node<*>?> { null }

@Suppress("TransitionPropertiesLabel")
abstract class Node<T : Parcelable>(
    val routingSource: RoutingSource<T, *>? = null,
    savedStateMap: SavedStateMap?,
) : Resolver<T>, Renderable {

    class ChildEntry<T>(
        val key: RoutingKey<T>,
        private val resolver: Resolver<T>,
        private val savedStateMap: SavedStateMap? = null,
    ) {
        internal val nodeLazy = lazy(LazyThreadSafetyMode.NONE) {
            resolver.resolve(key.routing, savedStateMap)
        }
        val node: Node<*> get() = nodeLazy.value
    }

    private val _children = MutableStateFlow(savedStateMap?.restoreChildren() ?: emptyMap())
    protected val children: StateFlow<Map<RoutingKey<T>, ChildEntry<T>>> = _children.asStateFlow()

    private var transitionsInBackgroundJob: Job? = null

    init {
        routingSource?.let { source ->
            // TODO Close context when destroyed (wait for lifecycle), memory leak now
            // TODO Fix potential multithreading
            GlobalScope.launch { source.syncChildrenWithRoutingSource() }
        }
    }

    private suspend fun RoutingSource<T, *>.syncChildrenWithRoutingSource() {
        all.collect { elements ->
            _children.update {
                val routingSourceKeys =
                    elements.mapTo(HashSet(elements.size)) { element -> element.key }
                val localKeys = it.keys
                val newKeys = routingSourceKeys.minus(localKeys)
                val removedKeys = localKeys.minus(routingSourceKeys)
                val mutableMap = it.toMutableMap()
                newKeys.forEach { key ->
                    mutableMap[key] = ChildEntry(key = key, resolver = this@Node)
                }
                removedKeys.forEach { key ->
                    mutableMap.remove(key)
                }
                mutableMap
            }
        }
    }

    private fun SavedStateMap.restoreChildren(): Map<RoutingKey<T>, ChildEntry<T>>? =
        (get(KEY_CHILDREN_STATE) as? Map<RoutingKey<T>, SavedStateMap>)?.mapValues {
            ChildEntry(it.key, this@Node, it.value)
        }

    fun childOrCreate(routingKey: RoutingKey<T>): ChildEntry<T> {
        return _children.updateAndGet {
            if (!it.containsKey(routingKey)) {
                it + (routingKey to ChildEntry(key = routingKey, resolver = this@Node))
            } else {
                it
            }
        }[routingKey] // .node
            ?: throw IllegalStateException("Child should be created in the previous step")
    }

    @Composable
    fun Compose() {
        CompositionLocalProvider(
            LocalNode provides this
        ) {
            routingSource?.let { source ->
                val canHandleBackPress by source.canHandleBackPress.collectAsState()
                BackHandler(canHandleBackPress) {
                    source.onBackPressed()
                }
            }

            TransitionsInBackgroundManager()

            View()
        }
    }

    /**
     * [RoutingSource.onTransitionFinished] is invoked by Composable function when animation is finished.
     * When application is in background, recomposition is paused, so we never invoke the callback.
     * Because we do not care about animations in background and still want to have
     * business-logic-driven routing in background, we need to instantly invoke the callback.
     */
    @Composable
    private fun TransitionsInBackgroundManager() {
        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(lifecycleOwner) {
            val observer = object : DefaultLifecycleObserver {
                override fun onStart(owner: LifecycleOwner) {
                    manageTransitionsInForeground()
                }

                override fun onStop(owner: LifecycleOwner) {
                    manageTransitionsInBackground()
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
                manageTransitionsInBackground()
            }
        }
    }

    private fun manageTransitionsInBackground() {
        if (transitionsInBackgroundJob != null || routingSource == null) return
        // TODO Close context when destroyed (wait for lifecycle), memory leak now
        transitionsInBackgroundJob = GlobalScope.launch {
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
    fun <S : Parcelable> AnimatedChildNode(
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
                    .mapValues { pair ->
                        pair.value.nodeLazy.let {
                            if (it.isInitialized()) it.value.onSaveInstanceState(scope)
                            else null
                        }
                    }
            if (childrenState.isNotEmpty()) map[KEY_CHILDREN_STATE] = childrenState
        }
        return map
    }

    companion object {
        const val KEY_ROUTING_SOURCE = "RoutingSource"
        const val KEY_CHILDREN_STATE = "ChildrenState"
    }

}
