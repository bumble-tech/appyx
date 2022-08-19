package com.bumble.appyx.navmodel.modal.operation

import com.bumble.appyx.navmodel.modal.Modal.TransitionState
import com.bumble.appyx.core.navigation.Operation

sealed interface ModalOperation<T> : Operation<T, TransitionState>
