package com.bumble.appyx.interactions.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.core.NavElement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

data class FrameModel<InteractionTarget>(
    val modifier: Modifier,
    val animationContainer: @Composable () -> Unit,
    val navElement: NavElement<InteractionTarget>,
    val progress: Flow<Float>,
    val visibleState: StateFlow<Boolean>
)


