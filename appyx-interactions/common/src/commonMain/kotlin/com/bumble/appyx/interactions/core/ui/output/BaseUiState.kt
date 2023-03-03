package com.bumble.appyx.interactions.core.ui.output

import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update

abstract class BaseUiState(
    isAnimatingFlows: List<Flow<Boolean>>
) {
    abstract val modifier: Modifier

    val isAnimating: Flow<Boolean> = combine(isAnimatingFlows) { booleanArray ->
        booleanArray.any { it }
    }

    private val _visibilityState by lazy { MutableStateFlow(isVisible()) }
    val visibilityState: StateFlow<Boolean> by lazy { _visibilityState }

    fun updateVisibilityState() {
        _visibilityState.update {
            isVisible()
        }
    }

    protected abstract fun isVisible(): Boolean
}
