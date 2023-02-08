package com.bumble.appyx.interactions.core.ui

import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.core.NavElement
import kotlinx.coroutines.flow.Flow

data class FrameModel<NavTarget>(
    val modifier: Modifier,
    val navElement: NavElement<NavTarget>,
    val progress: Float,
    val visibleState: Flow<Boolean>
)


