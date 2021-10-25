//package com.github.zsoltk.composeribs.core.routing.source.modal
//
//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.snapshots.SnapshotStateList
//import com.github.zsoltk.composeribs.core.routing.RoutingElement
//import com.github.zsoltk.composeribs.core.routing.RoutingKey
//import com.github.zsoltk.composeribs.core.routing.RoutingSource
//import com.github.zsoltk.composeribs.core.routing.source.modal.Modal.TransitionState.*
//import java.util.concurrent.atomic.AtomicInteger
//
//open class Modal<T>(
//    initialElements: List<T>,
//) : RoutingSource<T, Modal.TransitionState> {
//
//    data class LocalRoutingKey<T>(
//        override val routing: T,
//        val uuid: Int,
//    ) : RoutingKey<T>
//
//    enum class TransitionState {
//        CREATED, MODAL, FULL_SCREEN, DESTROYED
//    }
//
//    private val tmpCounter = AtomicInteger(1)
//
//    private lateinit var onRemoved: (RoutingKey<T>) -> Unit
//
//    override fun onRemoved(block: (RoutingKey<T>) -> Unit) {
//        onRemoved = block
//    }
//
//    override val elementsObservable: SnapshotStateList<RoutingElement<T, TransitionState>> =
//        mutableStateListOf<ModalElement<T>>().also {
//            it.addAll(
//                initialElements.map {
//                    ModalElement(
//                        key = LocalRoutingKey(it, tmpCounter.incrementAndGet()),
//                        fromState = CREATED,
//                        targetState = CREATED,
//                        onScreen = true
//                    )
//                }
//            )
//        }
//
//    override val pendingRemoval: SnapshotStateList<RoutingElement<T, TransitionState>> =
//        mutableStateListOf()
//
//    override val offScreen: List<ModalElement<T>>
//        get() = elementsObservable.filter { !it.onScreen }
//
//    override val onScreen: List<ModalElement<T>>
//        get() = elementsObservable.filter { it.onScreen }
//
//    fun add(element: T): ModalElement<T> {
//        val modalElement = ModalElement(
//                key = LocalRoutingKey(element, tmpCounter.incrementAndGet()),
//                fromState = CREATED,
//                targetState = CREATED,
//                onScreen = true
//        )
//        elementsObservable += modalElement
//
//        return modalElement
//    }
//
//    fun showModal(key: RoutingKey<T>) {
//        elementsObservable.toList().forEachIndexed { index, routingElement ->
//            if (routingElement.key == key) {
//                elementsObservable[index] = routingElement.copy(
//                    targetState = MODAL
//                )
//            }
//        }
//    }
//
//    fun dismissModal(key: RoutingKey<T>) {
//        elementsObservable.toList().forEachIndexed { index, routingElement ->
//            if (routingElement.key == key) {
//                elementsObservable[index] = routingElement.copy(
//                    targetState = CREATED
//                )
//            }
//        }
//    }
//
//    fun fullScreen(key: RoutingKey<T>) {
//        elementsObservable.toList().forEachIndexed { index, routingElement ->
//            if (routingElement.key == key) {
//                elementsObservable[index] = routingElement.copy(
//                    targetState = FULL_SCREEN
//                )
//            }
//        }
//    }
//
//    fun revert() {
//        elementsObservable.toList().forEachIndexed { index, routingElement ->
//            if (routingElement.targetState == FULL_SCREEN) {
//                elementsObservable[index] = routingElement.copy(
//                    targetState = MODAL
//                )
//            }
//            if (routingElement.targetState == MODAL) {
//                elementsObservable[index] = routingElement.copy(
//                    targetState = CREATED
//                )
//            }
//        }
//    }
//
//
//    fun destroy(key: RoutingKey<T>) {
//        elementsObservable.toList().forEachIndexed { index, routingElement ->
//            if (routingElement.key == key) {
//                elementsObservable[index] = routingElement.copy(
//                    targetState = DESTROYED
//                )
//            }
//        }
//    }
//
//    override fun onTransitionFinished(key: RoutingKey<T>) {
//        elementsObservable.toList().forEachIndexed { index, routingElement ->
//            if (routingElement.key == key) {
//                val targetState = routingElement.targetState
//                elementsObservable[index] = routingElement.copy(
//                    fromState = targetState
//                )
//
//                when (targetState) {
//                    DESTROYED -> remove(key)
//                    else -> {
//                    }
//                }
//            }
//        }
//    }
//
//    private fun remove(key: RoutingKey<T>) {
//        pendingRemoval.removeAll { it.key == key }
//        onRemoved(key)
//    }
//
//    override fun canHandleBackPress(): Boolean =
//        elementsObservable.any {
//            it.targetState == MODAL || it.targetState == FULL_SCREEN
//        }
//
//    override fun onBackPressed() {
//        revert()
//    }
//}
