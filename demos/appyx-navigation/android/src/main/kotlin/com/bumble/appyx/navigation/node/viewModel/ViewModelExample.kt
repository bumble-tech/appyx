package com.bumble.appyx.navigation.node.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.getAndUpdate

class ViewModelExample(startCounterValue: Int = 0) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState(startCounterValue))
    val uiState: StateFlow<UiState> = _uiState

    fun incrementCounter() {
        _uiState.getAndUpdate { value ->
            UiState(value.counter + 1)
        }
    }

    companion object {
        object StartCounterKey : CreationExtras.Key<Int>

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val startCounterValue = this[StartCounterKey] ?: 0
                ViewModelExample(
                    startCounterValue
                )
            }
        }
    }
}

data class UiState(val counter: Int)
