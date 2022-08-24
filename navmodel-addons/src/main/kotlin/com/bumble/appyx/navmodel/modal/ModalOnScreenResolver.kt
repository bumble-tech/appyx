package com.bumble.appyx.navmodel.modal

import com.bumble.appyx.navmodel.modal.Modal.TransitionState
import com.bumble.appyx.core.navigation.onscreen.OnScreenStateResolver

object ModalOnScreenResolver : OnScreenStateResolver<TransitionState> {
    override fun isOnScreen(state: TransitionState): Boolean =
        when (state) {
            TransitionState.MODAL,
            TransitionState.FULL_SCREEN,
            TransitionState.CREATED -> true
            TransitionState.DESTROYED -> false
        }
}
