package com.github.zsoltk.composeribs.client.container

import com.github.zsoltk.composeribs.core.routing.source.modal.Modal
import com.github.zsoltk.composeribs.core.routing.source.modal.ModalElement

class ContainerInteractorModal(
    private val modal: Modal<Container.Routing>
) {

    private lateinit var routingElement: ModalElement<Container.Routing>

    fun showModal() {
        routingElement = modal.add(Container.Routing.Child3)
        modal.showModal(routingElement.key)
    }

    fun makeFullScreen() {
        modal.fullScreen(routingElement.key)
    }
}
