package com.github.zsoltk.composeribs.core

import androidx.compose.runtime.mutableStateListOf
import java.util.UUID

typealias BackStackElement<T> = RoutingElement<T, BackStack.TransitionState>

class BackStack<T>(
    initialElement: T
) {

    private data class LocalRoutingKey<T>(
        override val routing: T,
        val uuid: UUID = UUID.randomUUID(),
    ) : RoutingKey<T>

    enum class TransitionState {
        ADDED, ACTIVE, INACTIVE, REMOVED
    }

    val elements = mutableStateListOf(
        BackStackElement(
            routingKey = LocalRoutingKey(initialElement),
            targetState = TransitionState.ACTIVE
        )
    )

    val currentRouting: T
        // TODO find last active rather
        get() = elements.last().routingKey.routing

    val offScreen: List<BackStackElement<T>>
        get() = if (elements.isEmpty()) emptyList() else elements.subList(0, elements.lastIndex)

    val onScreen: List<BackStackElement<T>>
        get() = listOf(elements.last())

    fun push(element: T) {
        elements += BackStackElement(
            routingKey = LocalRoutingKey(element),
            targetState = TransitionState.ACTIVE
        )
    }

    fun pop() {
        elements.removeLast()
    }
}
