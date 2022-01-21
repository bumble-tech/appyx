package com.bumble.appyx.v2.client.modal.operation

import com.bumble.appyx.v2.client.modal.Modal.TransitionState
import com.bumble.appyx.v2.core.routing.Operation

sealed interface ModalOperation<T> : Operation<T, TransitionState>
