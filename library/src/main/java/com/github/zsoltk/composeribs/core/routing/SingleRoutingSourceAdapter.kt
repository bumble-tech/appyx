package com.github.zsoltk.composeribs.core.routing

import com.github.zsoltk.composeribs.core.unsuspendedMap

class SingleRoutingSourceAdapter<Key, State>(
    private val routingSource: RoutingSource<Key, State>,
    private val onScreenResolver: OnScreenResolver<State>
) : RoutingSourceAdapter<Key, State> {

    override val onScreen = routingSource.elements.unsuspendedMap { elements ->
        elements.filter { element ->
            element.isOnScreen()
        }
    }

    override val offScreen = routingSource.elements.unsuspendedMap { elements ->
        elements.filterNot { element ->
            element.isOnScreen()
        }
    }

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
