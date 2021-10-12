package com.github.zsoltk.composeribs.core.routing.source.tiles

import com.github.zsoltk.composeribs.core.routing.RoutingElement
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.routing.source.tiles.Tiles.TransitionState.CREATED
import com.github.zsoltk.composeribs.core.routing.source.tiles.Tiles.TransitionState.DESTROYED
import com.github.zsoltk.composeribs.core.routing.source.tiles.Tiles.TransitionState.SELECTED
import com.github.zsoltk.composeribs.core.routing.source.tiles.Tiles.TransitionState.STANDARD
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.ObservableSource
import java.util.concurrent.atomic.AtomicInteger

open class Tiles<T>(
    initialElements: List<T>,
) : RoutingSource<T, Tiles.TransitionState> {

    data class LocalRoutingKey<T>(
        override val routing: T,
        val uuid: Int,
    ) : RoutingKey<T>

    enum class TransitionState {
        CREATED, STANDARD, SELECTED, DESTROYED
    }

    private val tmpCounter = AtomicInteger(1)

    private lateinit var onRemoved: (RoutingKey<T>) -> Unit

    override fun onRemoved(block: (RoutingKey<T>) -> Unit) {
        onRemoved = block
    }

    private val elementsRelay: BehaviorRelay<List<RoutingElement<T, TransitionState>>> =
        BehaviorRelay.createDefault(
            initialElements.map {
                TilesElement(
                    key = LocalRoutingKey(it, tmpCounter.incrementAndGet()),
                    fromState = CREATED,
                    targetState = STANDARD,
                    onScreen = true
                )
            }
        )
    override val elementsObservable: ObservableSource<List<RoutingElement<T, TransitionState>>> =
        elementsRelay

    override val offScreen: List<TilesElement<T>> = emptyList()

    override val onScreen: List<TilesElement<T>>
        get() = elementsRelay.value!!.filter { it.onScreen }

    override val all: List<RoutingElement<T, TransitionState>>
        get() = elementsRelay.value!!

    fun add(element: T) {
        elementsRelay.accept(
            elementsRelay.value!! + TilesElement(
                key = LocalRoutingKey(element, tmpCounter.incrementAndGet()),
                fromState = CREATED,
                targetState = STANDARD,
                onScreen = true
            )
        )
    }

    fun select(key: RoutingKey<T>) {
        elementsRelay.accept(
            elementsRelay.value!!.map {
                if (it.key == key && it.targetState == STANDARD) {
                    it.copy(targetState = SELECTED)
                } else {
                    it
                }
            }
        )
    }

    fun deselect(key: RoutingKey<T>) {
        elementsRelay.accept(
            elementsRelay.value!!.map {
                if (it.key == key && it.targetState == SELECTED) {
                    it.copy(targetState = STANDARD)
                } else {
                    it
                }
            }
        )
    }

    fun deselectAll() {
        elementsRelay.accept(
            elementsRelay.value!!.map {
                if (it.targetState == SELECTED) {
                    it.copy(targetState = STANDARD)
                } else {
                    it
                }
            }
        )
    }

    fun toggleSelection(key: RoutingKey<T>) {
        elementsRelay.accept(
            elementsRelay.value!!.map {
                if (it.key == key) {
                    it.copy(targetState = if (it.targetState == SELECTED) STANDARD else SELECTED)
                } else {
                    it
                }
            }
        )
    }

    fun removeSelected() {
        elementsRelay.accept(
            elementsRelay.value!!.map {
                if (it.targetState == SELECTED) {
                    it.copy(targetState = DESTROYED)
                } else {
                    it
                }
            }
        )
    }

    fun destroy(key: RoutingKey<T>) {
        elementsRelay.accept(
            elementsRelay.value!!.map {
                if (it.key == key) {
                    it.copy(targetState = DESTROYED)
                } else {
                    it
                }
            }
        )
    }

    override fun onTransitionFinished(key: RoutingKey<T>) {
        elementsRelay.accept(
            elementsRelay.value!!.mapNotNull {
                if (it.key == key) {
                    if (it.targetState == DESTROYED) {
                        onRemoved(it.key)
                        null
                    } else {
                        it.copy(fromState = it.targetState)
                    }
                } else {
                    it
                }
            }
        )
    }

    override fun canHandleBackPress(): Boolean =
        elementsRelay.value!!.any { it.targetState == SELECTED }

    override fun onBackPressed() {
        deselectAll()
    }

}
