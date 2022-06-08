package com.bumble.appyx.routingsourcedemos.modal

import com.bumble.appyx.routingsourcedemos.modal.Modal.TransitionState
import com.bumble.appyx.v2.core.routing.onscreen.OnScreenStateResolver

object ModalOnScreenResolver : OnScreenStateResolver<TransitionState> {
    override fun isOnScreen(state: TransitionState): Boolean =
        when (state) {
            TransitionState.MODAL,
            TransitionState.FULL_SCREEN,
            TransitionState.CREATED -> true
            TransitionState.DESTROYED -> false
        }
}
