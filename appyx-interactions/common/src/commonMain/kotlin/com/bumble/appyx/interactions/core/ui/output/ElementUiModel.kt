package com.bumble.appyx.interactions.core.ui.output

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.core.Element
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

data class ElementUiModel<InteractionTarget>(
    val element: Element<InteractionTarget>,
    val visibleState: StateFlow<Boolean>,
    val animationContainer: @Composable () -> Unit,
    val modifier: Modifier,
    val progress: Flow<Float>
)


