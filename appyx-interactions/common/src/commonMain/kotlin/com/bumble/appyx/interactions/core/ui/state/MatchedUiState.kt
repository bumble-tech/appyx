package com.bumble.appyx.interactions.core.ui.state

import com.bumble.appyx.interactions.core.Element

data class MatchedUiState<InteractionTarget, UiState: BaseUiState<UiState>>(
    val element: Element<InteractionTarget>,
    val uiState: UiState
)
