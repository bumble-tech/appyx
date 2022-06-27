package com.bumble.appyx.routingsource.modal.operation

import com.bumble.appyx.routingsource.modal.Modal.TransitionState
import com.bumble.appyx.core.routing.Operation

sealed interface ModalOperation<T> : Operation<T, TransitionState>
