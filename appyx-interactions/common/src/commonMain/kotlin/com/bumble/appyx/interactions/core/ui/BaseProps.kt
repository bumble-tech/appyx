package com.bumble.appyx.interactions.core.ui

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

abstract class BaseProps {

    protected val _visibilityState = MutableStateFlow(true)
    val visibilityState: Flow<Boolean> = _visibilityState

    protected abstract fun calculateVisibilityState()
}
