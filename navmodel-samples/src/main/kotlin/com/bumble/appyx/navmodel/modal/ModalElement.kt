package com.bumble.appyx.navmodel.modal

import com.bumble.appyx.navmodel.modal.Modal.TransitionState
import com.bumble.appyx.core.navigation.NavElement
import com.bumble.appyx.core.navigation.NavElements

typealias ModalElement<T> = NavElement<T, TransitionState>

typealias ModalElements<T> = NavElements<T, TransitionState>

