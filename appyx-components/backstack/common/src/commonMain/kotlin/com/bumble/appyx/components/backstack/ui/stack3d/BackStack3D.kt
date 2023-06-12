package com.bumble.appyx.components.backstack.ui.stack3d

class BackStack3d<InteractionTarget : Any>(
    uiContext: UiContext,
) : BaseMotionController<InteractionTarget, BackStackModel.State<InteractionTarget>, MutableUiState, TargetUiState>(
    uiContext = uiContext,
) {
    private val width = uiContext.transitionBounds.widthDp
    private val height = uiContext.transitionBounds.heightDp

}
