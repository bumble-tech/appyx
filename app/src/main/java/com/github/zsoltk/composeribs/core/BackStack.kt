package com.github.zsoltk.composeribs.core

class BackStack<T>(
    initialElement: T
) {

    val elements = mutableListOf(initialElement)

    val current: T
        get() = elements.last()

    val offScreen: List<T>
        get() = if (elements.isEmpty()) emptyList() else elements.subList(0, elements.lastIndex)

    val onScreen: List<T>
        get() = listOf(current)

    fun push(element: T) {
        elements += element
    }

    fun pop() {
        elements.removeLast()
    }
}
