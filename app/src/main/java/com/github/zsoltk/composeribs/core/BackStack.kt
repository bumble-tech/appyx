package com.github.zsoltk.composeribs.core

import androidx.compose.runtime.mutableStateListOf

class BackStack<T>(
    initialElement: T
) {

    private data class LocalRoutingKey<T>(
        val listIndex: Int,
        override val routing: T
    ) : RoutingKey<T>


    val elements = mutableStateListOf(initialElement)

    // TODO move to RoutingSource
    val active: List<RoutingKey<T>>
        get() = listOf(LocalRoutingKey(elements.lastIndex, current))

    val current: T
        get() = elements.last()

    val all: List<RoutingKey<T>>
        get() = elements.mapIndexed { idx, element ->
            LocalRoutingKey(idx, element)
        }

    val offScreen: List<RoutingKey<T>>
        get() = if (elements.isEmpty()) emptyList() else elements.subList(0, elements.lastIndex).mapIndexed { idx, element ->
            LocalRoutingKey(idx, element)
        }

    val onScreen: List<RoutingKey<T>>
        get() = listOf(
            LocalRoutingKey(elements.lastIndex, elements.last())
        )

    fun push(element: T) {
        elements += element
    }

    fun pop() {
        elements.removeLast()
    }
}
