package com.bumble.appyx.routingsourcedemos.modal

import com.bumble.appyx.routingsourcedemos.modal.Modal.TransitionState
import com.bumble.appyx.core.routing.RoutingElement
import com.bumble.appyx.core.routing.RoutingElements

typealias ModalElement<T> = RoutingElement<T, TransitionState>

typealias ModalElements<T> = RoutingElements<T, TransitionState>

