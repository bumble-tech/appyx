package com.github.zsoltk.composeribs.core

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.github.zsoltk.composeribs.core.routing.Renderable
import com.github.zsoltk.composeribs.core.routing.Resolver
import com.github.zsoltk.composeribs.core.routing.RoutingElement
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.routing.source.backstack.JumpToEndTransitionHandler
import com.github.zsoltk.composeribs.core.routing.transition.TransitionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch

val LocalNode = compositionLocalOf<Node<*>?> { null }

@Suppress("TransitionPropertiesLabel")
abstract class Node<T>(
    val routingSource: RoutingSource<T, *>? = null,
) : Resolver<T>, Renderable {

    class ChildEntry<T>(
        val key: RoutingKey<T>,
        private val resolver: Resolver<T>
    ) {
        val node: Node<*> by lazy(LazyThreadSafetyMode.NONE) { resolver.resolve(key.routing) }
    }

    private val _children = MutableStateFlow(emptyMap<RoutingKey<T>, ChildEntry<T>>())
    protected val children: StateFlow<Map<RoutingKey<T>, ChildEntry<T>>> = _children.asStateFlow()

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

            View()
        }
    }

    @Composable
    fun <S> ChildNode(
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

}
