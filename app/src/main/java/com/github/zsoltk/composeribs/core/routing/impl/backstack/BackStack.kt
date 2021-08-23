package com.github.zsoltk.composeribs.core.routing.impl.backstack

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.github.zsoltk.composeribs.core.routing.RoutingElement
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.routing.impl.backstack.BackStack.TransitionState.*
import java.util.concurrent.atomic.AtomicInteger

typealias BackStackElement<T> = RoutingElement<T, BackStack.TransitionState>

class BackStack<T>(
    initialElement: T,
) : RoutingSource<T, BackStack.TransitionState> {

    data class LocalRoutingKey<T>(
        override val routing: T,
        val uuid: Int,
    ) : RoutingKey<T>

    enum class TransitionState {
        CREATED, ON_SCREEN, STASHED_IN_BACK_STACK, DESTROYED;

        companion object {
            fun default() = CREATED
        }
    }

    private val tmpCounter = AtomicInteger(1)

    override val elements: SnapshotStateList<RoutingElement<T, TransitionState>> =
        mutableStateListOf(
            BackStackElement(
                key = LocalRoutingKey(initialElement, tmpCounter.incrementAndGet()),
                targetState = ON_SCREEN
            )
        )

    override val pendingRemoval: SnapshotStateList<RoutingElement<T, TransitionState>> =
        mutableStateListOf()

    val currentRouting: T
        get() = elements.last().key.routing

    override val offScreen: List<BackStackElement<T>>
        get() = emptyList() // if (elements.isEmpty()) emptyList() else elements.subList(0, elements.lastIndex)

    override val onScreen: List<BackStackElement<T>>
        get() = elements // listOf(elements.last())

    fun push(element: T) {
        with(elements) {
            elements[lastIndex] = elements[lastIndex].copy(
                targetState = STASHED_IN_BACK_STACK
            )
            elements += BackStackElement(
                key = LocalRoutingKey(element, tmpCounter.incrementAndGet()),
                targetState = CREATED
            )
            elements[lastIndex] = elements[lastIndex].copy(
                targetState = ON_SCREEN
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
                    targetState = ON_SCREEN
                )
            }
        }
    }

    override fun doRemove(key: RoutingKey<T>) {
        pendingRemoval.removeAll { it.key == key }
    }
}
