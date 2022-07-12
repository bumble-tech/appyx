package com.bumble.appyx.routingsource.modal.operation

import com.bumble.appyx.routingsource.modal.Modal
import com.bumble.appyx.routingsource.modal.Modal.TransitionState.DESTROYED
import com.bumble.appyx.routingsource.modal.ModalElements
import com.bumble.appyx.core.routing.RoutingKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class Destroy<T : Any>(
    private val key: RoutingKey<T>
) : ModalOperation<T> {

    override fun isApplicable(elements: ModalElements<T>) = true

    override fun invoke(elements: ModalElements<T>): ModalElements<T> {
        return elements.map {
            if (it.key == key) {
                it.transitionTo(
                    newTargetState = DESTROYED,
                    operation = this
                )
            } else {
                it
            }
        }
    }
}

fun <T : Any> Modal<T>.destroy(key: RoutingKey<T>) {
    accept(Destroy(key))
}
