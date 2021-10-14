package com.github.zsoltk.composeribs.core

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.Modifier
import com.github.zsoltk.composeribs.core.routing.Renderable
import com.github.zsoltk.composeribs.core.routing.Resolver
import com.github.zsoltk.composeribs.core.routing.RoutingElement
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.routing.source.backstack.JumpToEndTransitionHandler
import com.github.zsoltk.composeribs.core.routing.transition.TransitionHandler
import io.reactivex.Observable

val LocalNode = compositionLocalOf<Node<*>?> { null }


@Suppress("TransitionPropertiesLabel")
abstract class Node<T>(
    val routingSource: RoutingSource<T, *>? = null,
) : Resolver<T>, Renderable {

    class ChildEntry<T>(
        val key: RoutingKey<T>,
        private val resolver: Resolver<T>
    ) {
        val node: Node<*> by lazy { resolver.resolve(key.routing) }
    }

    val children = mutableStateMapOf<RoutingKey<T>, ChildEntry<T>>()
    private val keys = mutableStateListOf<RoutingKey<T>>()

    init {
        routingSource?.let {
            it.onRemoved { routingKey -> removeChild(routingKey) }
            it.elementsObservable.let {
                Observable.wrap(it).subscribe { elements ->
                    val routingSourceKeys = elements.map { it.key }
                    val localKeys = children.keys
                    val newKeys = routingSourceKeys.minus(localKeys)
                    val removedKeys = localKeys.minus(routingSourceKeys)
                    newKeys.forEach { routingKey ->
                        addChild(routingKey)
                    }

                    removedKeys.forEach { routingKey ->
                        removeChild(routingKey)
                    }
                }
            }
        }
    }

    private fun addChild(routingKey: RoutingKey<T>) {
        val entry = ChildEntry(
            key = routingKey,
            resolver = this
        )

        children[routingKey] = entry
        keys.add(routingKey)
    }

    private fun removeChild(routingKey: RoutingKey<T>) {
        children.remove(routingKey)
        keys.remove(routingKey)
    }

    fun childOrCreate(routingKey: RoutingKey<T>): ChildEntry<T> {
        if (!children.containsKey(routingKey)) {
            addChild(routingKey)
        }

        return children[routingKey]!! // .node
    }

    @Composable
    fun Compose() {
        CompositionLocalProvider(
            LocalNode provides this
        ) {
            routingSource?.let {
                BackHandler(it.canHandleBackPress()) {
                    it.onBackPressed()
                }
            }

            View(children)
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
