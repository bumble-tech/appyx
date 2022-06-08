package com.bumble.appyx.routingsourcedemos.modal.operation

import com.bumble.appyx.routingsourcedemos.modal.Modal
import com.bumble.appyx.routingsourcedemos.modal.Modal.TransitionState.FULL_SCREEN
import com.bumble.appyx.routingsourcedemos.modal.ModalElements
import com.bumble.appyx.v2.core.routing.RoutingKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class FullScreen<T : Any>(
    private val key: RoutingKey<T>
) : ModalOperation<T> {

    override fun isApplicable(elements: ModalElements<T>) = true

    override fun invoke(elements: ModalElements<T>): ModalElements<T> {
        return elements.map {
            if (it.key == key) {
                it.transitionTo(
                    targetState = FULL_SCREEN,
                    operation = this
                )
            } else {
                it
            }
        }
    }
}

fun <T : Any> Modal<T>.fullScreen(key: RoutingKey<T>) {
    accept(FullScreen(key))
}
