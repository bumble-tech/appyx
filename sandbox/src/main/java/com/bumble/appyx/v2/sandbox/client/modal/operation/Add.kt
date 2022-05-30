package com.bumble.appyx.v2.sandbox.client.modal.operation

import com.bumble.appyx.v2.sandbox.client.modal.Modal
import com.bumble.appyx.v2.sandbox.client.modal.Modal.TransitionState.CREATED
import com.bumble.appyx.v2.sandbox.client.modal.ModalElement
import com.bumble.appyx.v2.sandbox.client.modal.ModalElements
import com.bumble.appyx.v2.core.routing.RoutingKey
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Add<T : Any>(
    private val element: @RawValue T
) : ModalOperation<T> {

    override fun isApplicable(elements: ModalElements<T>) = true

    override fun invoke(elements: ModalElements<T>): ModalElements<T> {
        return elements + ModalElement(
            key = RoutingKey(element),
            fromState = CREATED,
            targetState = CREATED,
            operation = this
        )
    }
}

fun <T : Any> Modal<T>.add(element: T) {
    accept(Add(element))
}
