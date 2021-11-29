package com.github.zsoltk.composeribs.core.routing

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class OnScreenMapper<Key, State>(
    private val scope: CoroutineScope,
    private val onScreenStateResolver: OnScreenStateResolver<State>
) {

    fun resolveOnScreenElements(state: StateFlow<RoutingElements<Key, State>>): StateFlow<RoutingElements<Key, State>> =
        state
            .map { elements ->
                elements.filter { element ->
                    element.isOnScreen()
                }
            }
            .stateIn(scope, SharingStarted.Eagerly, emptyList())

    fun resolveOffScreenElements(state: StateFlow<RoutingElements<Key, State>>): StateFlow<RoutingElements<Key, State>> =
        state
            .map { elements ->
                elements.filterNot { element ->
                    element.isOnScreen()
                }
            }
            .stateIn(scope, SharingStarted.Eagerly, emptyList())

    private fun RoutingElement<Key, State>.isOnScreen(): Boolean =
        if (transitionHistory.isEmpty()) {
            onScreenStateResolver.isOnScreen(targetState) || onScreenStateResolver.isOnScreen(fromState)
        } else {
            transitionHistory.find { (fromState, toState) ->
                onScreenStateResolver.isOnScreen(fromState) || onScreenStateResolver.isOnScreen(toState)
            } != null
        }
}
