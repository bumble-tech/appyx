package com.bumble.appyx.interactions.core.ui

import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.core.NavElement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

data class FrameModel<NavTarget>(
    val modifier: Modifier,
    val animationModifier: Modifier,
    val navElement: NavElement<NavTarget>,
    val progress: StateFlow<Float>,
    val visibleState: Flow<Boolean>
)


