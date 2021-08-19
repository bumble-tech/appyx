package com.github.zsoltk.composeribs.core

import androidx.compose.runtime.mutableStateListOf

class BackStack<T>(
    initialElement: T
) {

    val elements = mutableStateListOf(initialElement)

    // TODO move to RoutingSource
    val active: List<RoutingKey<T>>
        get() = listOf(RoutingKey(elements.lastIndex, current))

    val current: T
        get() = elements.last()

    val all: List<RoutingKey<T>>
        get() = elements.mapIndexed { idx, element ->
            RoutingKey(idx, element)
        }

    val offScreen: List<RoutingKey<T>>
        get() = if (elements.isEmpty()) emptyList() else elements.subList(0, elements.lastIndex).mapIndexed { idx, element ->
            RoutingKey(idx, element)
        }

    val onScreen: List<RoutingKey<T>>
        get() = listOf(
            RoutingKey(elements.lastIndex, elements.last())
        )

    fun push(element: T) {
        elements += element
    }

    fun pop() {
        elements.removeLast()
    }
}
