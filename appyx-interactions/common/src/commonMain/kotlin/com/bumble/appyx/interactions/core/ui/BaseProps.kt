package com.bumble.appyx.interactions.core.ui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

abstract class BaseProps {

    private val _visibilityState by lazy { MutableStateFlow(isVisible()) }
    val visibilityState: StateFlow<Boolean>  by lazy {  _visibilityState }

    fun updateVisibilityState() {
        _visibilityState.update {
            isVisible()
        }
    }

    protected abstract fun isVisible(): Boolean
}
