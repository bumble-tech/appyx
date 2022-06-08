package com.bumble.appyx.routingsourcedemos.modal.operation

import com.bumble.appyx.routingsourcedemos.modal.Modal.TransitionState
import com.bumble.appyx.v2.core.routing.Operation

sealed interface ModalOperation<T> : Operation<T, TransitionState>
