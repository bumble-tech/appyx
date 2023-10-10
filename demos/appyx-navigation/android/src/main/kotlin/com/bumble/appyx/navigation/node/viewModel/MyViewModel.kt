package com.bumble.appyx.navigation.node.viewModel

import com.bumble.appyx.utils.viewmodel.integration.IntegrationPointViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MyViewModel : IntegrationPointViewModel() {
    private val stateFlow = MutableStateFlow(0)
    val flow: Flow<Int> = stateFlow

    init {
        viewModelScope.launch {
            while (true) {
                stateFlow.getAndUpdate { value ->
                    value + 1
                }
                delay(1000)
            }
        }
    }
}
