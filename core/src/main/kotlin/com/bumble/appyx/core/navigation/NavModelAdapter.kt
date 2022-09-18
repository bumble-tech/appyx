package com.bumble.appyx.core.navigation

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow

@Stable
interface NavModelAdapter<Routing, State> {

    val screenState: StateFlow<ScreenState<Routing, out State>>

    data class ScreenState<Routing, State>(
        val onScreen: NavElements<Routing, out State> = emptyList(),
        val offScreen: NavElements<Routing, out State> = emptyList(),
    )

}
