package com.bumble.appyx.sandbox.client.modal.operation

import com.bumble.appyx.sandbox.client.modal.Modal.TransitionState
import com.bumble.appyx.core.routing.Operation

sealed interface ModalOperation<T> : Operation<T, TransitionState>
