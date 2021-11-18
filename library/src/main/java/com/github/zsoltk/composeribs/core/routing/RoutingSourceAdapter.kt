package com.github.zsoltk.composeribs.core.routing

import com.github.zsoltk.composeribs.core.unsuspendedMap
import kotlinx.coroutines.flow.StateFlow

class RoutingSourceAdapterImpl<Key, State>(
    private val routingSource: RoutingSource<Key, State>,
    private val routingSourceResolver: OnScreenResolver<State>
) : RoutingSourceAdapter<Key, State> {

    override val onScreen = routingSource.elements.unsuspendedMap { elements ->
        elements.filter { element ->
            if (element.transitionHistory.isEmpty()) {
                routingSourceResolver.isOnScreen(element.targetState)
            } else {
                routingSourceResolver.isOnScreen(element.fromState)
                        || routingSourceResolver.isOnScreen(element.targetState)
            }
        }
    }

    override val offScreen = routingSource.elements.unsuspendedMap { elements ->
        elements.filterNot { element ->
            if (element.transitionHistory.isEmpty()) {
                routingSourceResolver.isOnScreen(element.targetState)
            } else {
                routingSourceResolver.isOnScreen(element.fromState)
                        || routingSourceResolver.isOnScreen(element.targetState)
            }
        }
    }

    override fun onTransitionFinished(key: RoutingKey<Key>) {
        routingSource.onTransitionFinished(key)
    }
}

interface RoutingSourceAdapter<Key, State> {

    val onScreen: StateFlow<RoutingElements<Key, out State>>

    val offScreen: StateFlow<RoutingElements<Key, out State>>

    fun onTransitionFinished(key: RoutingKey<Key>)
}