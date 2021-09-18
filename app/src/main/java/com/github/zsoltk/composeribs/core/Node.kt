package com.github.zsoltk.composeribs.core

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.github.zsoltk.composeribs.core.routing.Resolver
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.SubtreeController

@Suppress("TransitionPropertiesLabel")
abstract class Node<T>(
    private val subtreeController: SubtreeController<T, *>? = null
) : Resolver<T> {

    data class ChildEntry<T>(
        val key: RoutingKey<T>,
        val node: Node<*>? = null,
        val onScreen: Boolean,
    )

    // TODO consider if it's possible to merge into ChildEntry (and if it's wise at all to do so)
    data class ViewChildEntry<T>(
        val key: RoutingKey<*>,
        val composable: Node<T>,
        val modifier: Modifier = Modifier, // TODO var
    )

    private val children = mutableMapOf<RoutingKey<T>, ChildEntry<T>>()
    private val keys = mutableStateListOf<RoutingKey<T>>()

    init {
        subtreeController?.routingSource?.onRemoved {
            children.remove(it)
            keys.remove(it)
        }
    }

    @Composable
    fun Compose() {
        subtreeController?.routingSource?.let {
            BackHandler(it.canHandleBackPress()) {
                it.onBackPressed()
            }
        }

        subtreeController?.let {
            ViewWithSubtree(it)
        } ?: run {
            View(emptyList())
        }
    }

    @Composable
    private fun ViewWithSubtree(
        subtreeController: SubtreeController<T, *>
    ) {
        // FIXME
//        LaunchedEffect(elements) {
            subtreeController.manageOffScreen()
            subtreeController.manageOnScreen()
//        }

        val composedViews = keys
            .map { children[it]!! }
            .filter { it.onScreen }
            .map { childEntry ->
                key(childEntry.key) {
                    val node = childEntry.node
                    requireNotNull(node) { "Node for on-screen entry should have been resolved" }

                    val modifier = subtreeController.getModifierSnapshot(
                        key = childEntry.key
                    )

                    // TODO optimise (only update modifier, don't create new object)
                    ViewChildEntry(
                        key = childEntry.key,
                        composable = node,
                        modifier =  modifier
                    )
                }
            }

        View(composedViews)
    }

    private fun SubtreeController<T, *>.manageOffScreen() {
        routingSource.offScreen.forEach { element ->
            val routingKey = element.key

            children[routingKey]?.let { entry ->
                if (entry.onScreen) {
                    children[routingKey] = entry.copy(onScreen = false)
                }
            } ?: run {
                val entry = ChildEntry(
                    key = routingKey,
                    onScreen = false
                )
                children[routingKey] = entry
                keys.add(routingKey)
            }
        }
    }

    private fun SubtreeController<T, *>.manageOnScreen() {
        routingSource.onScreen.forEach { element ->
            val routingKey = element.key

            children[routingKey]?.let { entry ->
                if (!entry.onScreen) {
                    children[routingKey] = entry.copy(
                        onScreen = true,
                        node = entry.node ?: resolve(routingKey.routing)
                    )
                }
            } ?: run {
                val entry = ChildEntry(
                    key = routingKey,
                    node = resolve(routingKey.routing),
                    onScreen = true
                )

                children[routingKey] = entry
                keys.add(routingKey)
            }
        }
    }

    // FIXME with Scope
    var viewChildren: List<ViewChildEntry<*>> = listOf()

    @Composable
    fun View(children: List<ViewChildEntry<*>>) {
        // FIXME with Scope
        this.viewChildren = children
        View()
    }

    @Composable
    open fun View() {
    }

    @Composable
    inline fun <reified V : T> placeholder(
        modifier: (RoutingKey<T>) -> Modifier = { Modifier },
        filter: (RoutingKey<out V>) -> Boolean = { true },
    ) {
        val filtered = remember(viewChildren, V::class.java) {
            viewChildren.filter {
                (it.key.routing is V || it.key.routing!!::class.java.isAssignableFrom(V::class.java)) &&
                    filter.invoke(it.key as RoutingKey<V>)
            }
        }

        filtered.forEach { child ->
            key(child.key) {
                val clientModifier = modifier(child.key as RoutingKey<T>)
                Box(modifier = clientModifier.then(child.modifier)) {
                    child.composable.Compose()
                }
            }
        }
    }
}
