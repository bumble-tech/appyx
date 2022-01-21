package com.bumble.appyx.v2.sandbox.client.modal

import com.bumble.appyx.v2.sandbox.client.modal.Modal.TransitionState
import com.bumble.appyx.v2.core.routing.RoutingElement
import com.bumble.appyx.v2.core.routing.RoutingElements

typealias ModalElement<T> = RoutingElement<T, TransitionState>

typealias ModalElements<T> = RoutingElements<T, TransitionState>

