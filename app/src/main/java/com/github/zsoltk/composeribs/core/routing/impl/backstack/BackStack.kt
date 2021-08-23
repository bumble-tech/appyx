package com.github.zsoltk.composeribs.core.routing.impl.backstack

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.github.zsoltk.composeribs.core.routing.RoutingElement
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.routing.impl.backstack.BackStack.TransitionState.*
import java.util.concurrent.atomic.AtomicInteger

open class BackStack<T>(
    initialElement: T,
) : RoutingSource<T, BackStack.TransitionState> {

    data class LocalRoutingKey<T>(
        override val routing: T,
        val uuid: Int,
    ) : RoutingKey<T>

    enum class TransitionState {
        CREATED, ON_SCREEN, STASHED_IN_BACK_STACK, DESTROYED
    }

    private val tmpCounter = AtomicInteger(1)

    override val elements: SnapshotStateList<RoutingElement<T, TransitionState>> =
        mutableStateListOf(
            BackStackElement(
                key = LocalRoutingKey(initialElement, tmpCounter.incrementAndGet()),
                fromState = ON_SCREEN,
                targetState = ON_SCREEN,
                onScreen = true
            )
        )

    override val pendingRemoval: SnapshotStateList<RoutingElement<T, TransitionState>> =
        mutableStateListOf()

    val currentRouting: T
        get() = elements.last().key.routing

    override val offScreen: List<BackStackElement<T>>
        get() = elements.filter { !it.onScreen }

    override val onScreen: List<BackStackElement<T>>
        get() = elements.filter { it.onScreen }

    fun push(element: T) {
        with(elements) {
            elements[lastIndex] = elements[lastIndex].copy(
                targetState = STASHED_IN_BACK_STACK
            )
            elements += BackStackElement(
                key = LocalRoutingKey(element, tmpCounter.incrementAndGet()),
                fromState = CREATED,
                targetState = ON_SCREEN,
                onScreen = true
            )
        }
    }

    fun pop() {
        with(elements) {
            if (size > 1) {
                val popped = elements.removeLast()
                pendingRemoval.add(
                    popped.copy(
                        targetState = DESTROYED
                    )
                )
                elements[lastIndex] = elements[lastIndex].copy(
                    fromState = STASHED_IN_BACK_STACK,
                    targetState = ON_SCREEN,
                    onScreen = true
                )
            }
        }
    }

    override fun doMarkOffScreen(key: RoutingKey<T>) {
        elements.toList().forEachIndexed { index, routingElement ->
            if (routingElement.key == key) {
                elements[index] = routingElement.copy(
                    onScreen = false
                )
            }
        }
    }

    override fun doRemove(key: RoutingKey<T>) {
        pendingRemoval.removeAll { it.key == key }
    }
}
