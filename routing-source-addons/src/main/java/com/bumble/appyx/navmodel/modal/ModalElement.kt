package com.bumble.appyx.navmodel.modal

import com.bumble.appyx.navmodel.modal.Modal.TransitionState
import com.bumble.appyx.core.navigation.RoutingElement
import com.bumble.appyx.core.navigation.RoutingElements

typealias ModalElement<T> = RoutingElement<T, TransitionState>

typealias ModalElements<T> = RoutingElements<T, TransitionState>

