package com.github.zsoltk.composeribs.core

import androidx.compose.runtime.mutableStateListOf
import com.github.zsoltk.composeribs.core.BackStack.TransitionState.*
import java.util.concurrent.atomic.AtomicInteger

typealias BackStackElement<T> = RoutingElement<T, BackStack.TransitionState>

class BackStack<T>(
    initialElement: T,
) {

    data class LocalRoutingKey<T>(
        override val routing: T,
        val uuid: Int,
    ) : RoutingKey<T>

    enum class TransitionState {
        ADDED, ACTIVE, INACTIVE, REMOVED
    }

    private val tmpCounter = AtomicInteger(1)

    val elements = mutableStateListOf(
        BackStackElement(
            routingKey = LocalRoutingKey(initialElement, tmpCounter.incrementAndGet()),
            targetState = ACTIVE
        )
    )

    val pendingRemoval = mutableStateListOf<BackStackElement<T>>()

    val currentRouting: T
        // TODO find last active rather
        get() = elements.last().routingKey.routing

    val offScreen: List<BackStackElement<T>>
        get() = emptyList() // if (elements.isEmpty()) emptyList() else elements.subList(0, elements.lastIndex)

    val onScreen: List<BackStackElement<T>>
        get() = elements // listOf(elements.last())

    fun push(element: T) {
        with(elements) {
            elements[lastIndex] = elements[lastIndex].copy(
                targetState = INACTIVE
            )
            elements += BackStackElement(
                routingKey = LocalRoutingKey(element, tmpCounter.incrementAndGet()),
                targetState = ADDED
            )
            elements[lastIndex] = elements[lastIndex].copy(
                targetState = ACTIVE
            )
        }
    }

    fun pop() {
        with(elements) {
            if (size > 1) {
                val popped = elements.removeLast()
                pendingRemoval.add(
                    popped.copy(
                        targetState = REMOVED
                    )
                )
                elements[lastIndex] = elements[lastIndex].copy(
                    targetState = ACTIVE
                )
            }
        }
    }

    fun doRemove(key: RoutingKey<T>) {
        pendingRemoval.removeAll { it.routingKey == key }
    }
}
