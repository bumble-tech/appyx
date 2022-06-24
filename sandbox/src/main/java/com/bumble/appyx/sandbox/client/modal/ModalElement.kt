package com.bumble.appyx.sandbox.client.modal

import com.bumble.appyx.sandbox.client.modal.Modal.TransitionState
import com.bumble.appyx.core.routing.RoutingElement
import com.bumble.appyx.core.routing.RoutingElements

typealias ModalElement<T> = RoutingElement<T, TransitionState>

typealias ModalElements<T> = RoutingElements<T, TransitionState>

