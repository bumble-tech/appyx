package com.bumble.appyx.routingsourcedemos.modal.operation

import com.bumble.appyx.routingsourcedemos.modal.Modal.TransitionState
import com.bumble.appyx.core.routing.Operation

sealed interface ModalOperation<T> : Operation<T, TransitionState>
