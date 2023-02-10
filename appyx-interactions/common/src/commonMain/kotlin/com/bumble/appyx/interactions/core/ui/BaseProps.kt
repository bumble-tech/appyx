package com.bumble.appyx.interactions.core.ui

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

abstract class BaseProps {

    private val _visibilityState = MutableStateFlow(true)
    val visibilityState: Flow<Boolean> = _visibilityState

    protected fun updateVisibilityState() {
        _visibilityState.update {
            isVisible()
        }
    }

    protected abstract fun isVisible(): Boolean
}
