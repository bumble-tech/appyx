package com.github.zsoltk.composeribs.core.composable

import androidx.compose.animation.core.Transition
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import com.github.zsoltk.composeribs.core.node.ParentNode
import com.github.zsoltk.composeribs.core.routing.RoutingSourceAdapter
import com.github.zsoltk.composeribs.core.routing.transition.TransitionBounds
import com.github.zsoltk.composeribs.core.routing.transition.TransitionDescriptor
import com.github.zsoltk.composeribs.core.routing.transition.TransitionHandler
import com.github.zsoltk.composeribs.core.routing.transition.TransitionParams
import kotlinx.coroutines.flow.map
import kotlin.reflect.KClass

@Composable
fun <T : Any, S> Subtree(
    modifier: Modifier,
    transitionHandler: TransitionHandler<T, S>,
    adapter: RoutingSourceAdapter<T, S>,
    block: @Composable SubtreeTransitionScope<T, S>.() -> Unit
) {
    BoxWithConstraints(modifier) {
        block(
            SubtreeTransitionScope(
                transitionHandler,
                TransitionParams(
                    bounds = TransitionBounds(
                        width = maxWidth,
                        height = maxHeight
                    )
                ),
                adapter
            )
        )
    }
}

class SubtreeTransitionScope<T : Any, S>(
    private val transitionHandler: TransitionHandler<T, S>,
    private val transitionParams: TransitionParams,
    private val adapter: RoutingSourceAdapter<T, S>
) {

    @Composable
    inline fun <reified V : T> ParentNode<T>.children(
        noinline block: @Composable ChildTransitionScope<S>.(child: @Composable () -> Unit) -> Unit,
    ) {
        children(V::class, block)
    }

    @Composable
    fun ParentNode<T>.children(
        clazz: KClass<out T>,
        block: @Composable ChildTransitionScope<S>.(child: @Composable () -> Unit) -> Unit,
    ) {
//        // TODO consider
//        val node = LocalNode.current?.let { it as Node<T> }
//            ?: error("Subtree can't be invoked outside of a Node's Composable context")

        val children by adapter.onScreen
            .map { list ->
                list
                    .filter { clazz.isInstance(it.key.routing) }
                    .map { it to childOrCreate(it.key) }
            }
            .collectAsState(emptyList())

        val saveableStateHolder = rememberSaveableStateHolder()
        children.forEach { (routingElement, childEntry) ->
            key(childEntry.key.id) {
                saveableStateHolder.SaveableStateProvider(key = routingElement.key) {
                    val transitionScope =
                        transitionHandler.handle(
                            descriptor = TransitionDescriptor(
                                params = transitionParams,
                                operation = routingElement.operation,
                                element = routingElement.key.routing,
                                fromState = routingElement.fromState,
                                toState = routingElement.targetState
                            ),
                            onTransitionFinished = {
                                adapter.onTransitionFinished(
                                    childEntry.key
                                )
                            })

                    transitionScope.block(
                        child = {
                            CompositionLocalProvider(LocalTransitionModifier provides transitionScope.transitionModifier) {
                                childEntry.node.Compose()
                            }
                        }
                    )
                }
            }
        }
    }
}

val LocalTransitionModifier = compositionLocalOf<Modifier?> { null }

interface ChildTransitionScope<S> {
    val transition: Transition<S>
    val transitionModifier: Modifier
}

internal class ChildTransitionScopeImpl<S>(
    override val transition: Transition<S>,
    override val transitionModifier: Modifier
) : ChildTransitionScope<S>
