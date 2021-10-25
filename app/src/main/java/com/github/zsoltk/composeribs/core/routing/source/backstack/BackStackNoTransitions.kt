package com.github.zsoltk.composeribs.core.routing.source.backstack
/*

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.github.zsoltk.composeribs.core.routing.RoutingElement
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState.*
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.ObservableSource
import java.util.concurrent.atomic.AtomicInteger

open class BackStackNoTransitions<T>(
    initialElement: T,
) : RoutingSource<T, TransitionState> {

    data class LocalRoutingKey<T>(
        override val routing: T,
        val uuid: Int,
    ) : RoutingKey<T>

    private val tmpCounter = AtomicInteger(1)

    private val elements = mutableListOf(
        BackStackElement(
            key = LocalRoutingKey(initialElement, tmpCounter.incrementAndGet()),
            fromState = ON_SCREEN,
            targetState = ON_SCREEN,
            onScreen = true
        )
    )

    private val elementsRelay: BehaviorRelay<List<RoutingElement<T, TransitionState>>> = BehaviorRelay.createDefault(elements)
    override val elementsObservable: ObservableSource<List<RoutingElement<T, TransitionState>>> = elementsRelay

    val currentRouting: T
        get() = elements.last().key.routing

    override val all: List<RoutingElement<T, TransitionState>>
        get() = elements

    override val offScreen: Elements<T>
        get() = elements.filter { !it.onScreen }

    override val onScreen: Elements<T>
        get() = elements.filter { it.onScreen }

    fun push(element: T) {
        with(elements) {
            elements[lastIndex] = elements[lastIndex].copy(
                targetState = STASHED_IN_BACK_STACK,
                onScreen = false
            )
            elements += BackStackElement(
                key = LocalRoutingKey(element, tmpCounter.incrementAndGet()),
                fromState = CREATED,
                targetState = ON_SCREEN,
                onScreen = true
            )
        }

        elementsRelay.accept(elements)
    }

    fun pop() {
        with(elements) {
            if (size > 1) {
                val popped = elements.removeLast()
                elements[lastIndex] = elements[lastIndex].copy(
                    fromState = STASHED_IN_BACK_STACK,
                    targetState = ON_SCREEN,
                    onScreen = true
                )
            }
        }

        elementsRelay.accept(elements)
    }

    // TODO cleanup interface, this shouldn't be here
    override fun onTransitionFinished(key: RoutingKey<T>) {
    }

    // TODO cleanup interface, this shouldn't be here
    override fun onRemoved(block: (RoutingKey<T>) -> Unit) {
    }

    override fun canHandleBackPress(): Boolean =
        elements.size > 1

    override fun onBackPressed() {
        pop()
    }
}
*/
