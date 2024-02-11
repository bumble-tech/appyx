package com.bumble.appyx.interactions.ui.state

import com.bumble.appyx.interactions.model.Element

data class MatchedTargetUiState<InteractionTarget, TargetUiState>(
    val element: Element<InteractionTarget>,
    val targetUiState: TargetUiState
)
