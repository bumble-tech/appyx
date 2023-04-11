package com.bumble.appyx.interactions.core.ui.state

import com.bumble.appyx.interactions.core.Element

data class MatchedTargetUiState<InteractionTarget, TargetUiState>(
    val element: Element<InteractionTarget>,
    val targetUiState: TargetUiState
)
