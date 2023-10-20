package com.bumble.appyx.navigation.node.viewModel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.get
import com.bumble.appyx.utils.viewmodel.integration.IntegrationPointViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.getAndUpdate

class ViewModelExample : IntegrationPointViewModel() {
    private val _uiState = MutableStateFlow(UiState(0))
    val uiState: Flow<UiState> = _uiState

    fun incrementCounter() {
        _uiState.getAndUpdate { value ->
            UiState(value.counter + 1)
        }
    }

    companion object {
        fun getInstance(viewModelStoreOwner: ViewModelStoreOwner): ViewModelExample {
            return ViewModelProvider(viewModelStoreOwner).get()
        }
    }
}

data class UiState(val counter: Int)
