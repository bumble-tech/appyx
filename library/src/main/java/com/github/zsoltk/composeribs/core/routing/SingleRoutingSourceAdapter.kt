package com.github.zsoltk.composeribs.core.routing

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SingleRoutingSourceAdapter<Key, State>(
    scope: CoroutineScope,
    private val routingSource: RoutingSource<Key, State>,
    private val onScreenResolver: OnScreenResolver<State>
) : RoutingSourceAdapter<Key, State> {

    override val elements: StateFlow<RoutingElements<Key, out State>> = routingSource.elements

    override val onScreen: StateFlow<RoutingElements<Key, out State>> =
        routingSource
            .elements
            .map { elements ->
                elements.filter { element ->
                    element.isOnScreen()
                }
            }
            .stateIn(scope, SharingStarted.Eagerly, emptyList())

    override val offScreen: StateFlow<RoutingElements<Key, out State>> =
        routingSource
            .elements
            .map { elements ->
                elements.filterNot { element ->
                    element.isOnScreen()
                }
            }
            .stateIn(scope, SharingStarted.Eagerly, emptyList())

    override fun onTransitionFinished(key: RoutingKey<Key>) {
        routingSource.onTransitionFinished(key)
    }

    override fun isElementOnScreen(key: RoutingKey<Key>): Boolean =
        onScreen.value.find { it.key == key } != null

    private fun RoutingElement<Key, out State>.isOnScreen(): Boolean =
        if (transitionHistory.isEmpty()) {
            onScreenResolver.isOnScreen(targetState) || onScreenResolver.isOnScreen(fromState)
        } else {
            transitionHistory.find { pair ->
                onScreenResolver.isOnScreen(pair.first)
                        || onScreenResolver.isOnScreen(pair.second)
            } != null
        }
}
