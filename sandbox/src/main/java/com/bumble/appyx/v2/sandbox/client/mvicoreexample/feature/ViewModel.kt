package com.bumble.appyx.v2.sandbox.client.mvicoreexample.feature

sealed class ViewModel {
    data class InitialState(val stateName: String) : ViewModel()
    data class Loaded(val stateName: String) : ViewModel()
    object Loading : ViewModel()
}
