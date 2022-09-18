package com.bumble.appyx.core.navigation

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow

@Stable
interface NavModelAdapter<NavTarget, State> {

    val screenState: StateFlow<ScreenState<NavTarget, out State>>

    data class ScreenState<NavTarget, State>(
        val onScreen: NavElements<NavTarget, out State> = emptyList(),
        val offScreen: NavElements<NavTarget, out State> = emptyList(),
    )

}
