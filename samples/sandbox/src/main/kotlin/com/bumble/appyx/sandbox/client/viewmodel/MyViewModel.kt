package com.bumble.appyx.sandbox.client.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch

class MyViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

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

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                MyViewModel(
                    savedStateHandle = savedStateHandle
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}
