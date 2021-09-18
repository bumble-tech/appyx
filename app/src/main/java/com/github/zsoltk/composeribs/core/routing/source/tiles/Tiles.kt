package com.github.zsoltk.composeribs.core.routing.source.tiles

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.github.zsoltk.composeribs.core.routing.RoutingElement
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.routing.source.tiles.Tiles.TransitionState.*
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

    override val elements: SnapshotStateList<RoutingElement<T, TransitionState>> =
        mutableStateListOf<TilesElement<T>>().also {
            it.addAll(
                initialElements.map {
                    TilesElement(
                        key = LocalRoutingKey(it, tmpCounter.incrementAndGet()),
                        fromState = CREATED,
                        targetState = STANDARD,
                        onScreen = true
                    )
                }
            )
        }

    override val pendingRemoval: SnapshotStateList<RoutingElement<T, TransitionState>> =
        mutableStateListOf()

    override val offScreen: List<TilesElement<T>>
        get() = elements.filter { !it.onScreen }

    override val onScreen: List<TilesElement<T>>
        get() = elements.filter { it.onScreen }

    fun add(element: T) {
        elements += TilesElement(
            key = LocalRoutingKey(element, tmpCounter.incrementAndGet()),
            fromState = CREATED,
            targetState = STANDARD,
            onScreen = true
        )
    }

    fun select(key: RoutingKey<T>) {
        elements.toList().forEachIndexed { index, routingElement ->
            if (routingElement.key == key) {
                elements[index] = routingElement.copy(
                    targetState = SELECTED
                )
            }
        }
    }

    fun deselect(key: RoutingKey<T>) {
        elements.toList().forEachIndexed { index, routingElement ->
            if (routingElement.key == key) {
                elements[index] = routingElement.copy(
                    targetState = STANDARD
                )
            }
        }
    }

    fun deselectAll() {
        elements.toList().forEachIndexed { index, routingElement ->
            if (routingElement.targetState == SELECTED) {
                elements[index] = routingElement.copy(
                    targetState = STANDARD
                )
            }
        }
    }

    fun toggleSelection(key: RoutingKey<T>) {
        elements.toList().forEachIndexed { index, routingElement ->
            if (routingElement.key == key) {
                elements[index] = routingElement.copy(
                    targetState = if (routingElement.targetState == STANDARD) SELECTED else STANDARD
                )
            }
        }
    }

    fun removeSelected() {
        elements.toList().forEachIndexed { index, routingElement ->
            if (routingElement.targetState == SELECTED) {
                elements.remove(routingElement)
                pendingRemoval.add(
                    routingElement.copy(
                        targetState = DESTROYED
                    )
                )
            }
        }
    }

    fun destroy(key: RoutingKey<T>) {
        elements.toList().forEachIndexed { index, routingElement ->
            if (routingElement.key == key) {
                elements[index] = routingElement.copy(
                    targetState = DESTROYED
                )
            }
        }
    }

    override fun onTransitionFinished(key: RoutingKey<T>, targetState: TransitionState) {
        when (targetState) {
            DESTROYED -> remove(key)
            else -> {
            }
        }
    }

    private fun remove(key: RoutingKey<T>) {
        pendingRemoval.removeAll { it.key == key }
        onRemoved(key)
    }

    override fun canHandleBackPress(): Boolean =
        elements.any {
            it.targetState == SELECTED
        }

    override fun onBackPressed() {
        deselectAll()
    }
}
