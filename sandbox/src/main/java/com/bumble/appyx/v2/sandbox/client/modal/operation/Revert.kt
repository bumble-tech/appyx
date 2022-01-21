package com.bumble.appyx.v2.sandbox.client.modal.operation

import com.bumble.appyx.v2.sandbox.client.modal.Modal
import com.bumble.appyx.v2.sandbox.client.modal.Modal.TransitionState.FULL_SCREEN
import com.bumble.appyx.v2.sandbox.client.modal.Modal.TransitionState.MODAL
import com.bumble.appyx.v2.sandbox.client.modal.ModalElements
import kotlinx.parcelize.Parcelize

@Parcelize
class Revert<T : Any> : ModalOperation<T> {

    override fun isApplicable(elements: ModalElements<T>): Boolean =
        true

    override fun invoke(elements: ModalElements<T>): ModalElements<T> {
        return elements.map {
            when (it.targetState) {
                FULL_SCREEN -> {
                    it.transitionTo(
                        targetState = MODAL,
                        operation = this
                    )
                }
                MODAL -> {
                    it.transitionTo(
                        targetState = FULL_SCREEN,
                        operation = this
                    )
                }
                else -> {
                    it
                }
            }
        }
    }
}

fun <T : Any> Modal<T>.revert() {
    accept(Revert())
}
