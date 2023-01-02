package com.bumble.appyx.core.navigation

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow

@Stable
interface NavModelAdapter<NavTarget : Parcelable, State : Parcelable> {

    val screenState: StateFlow<ScreenState<NavTarget, out State>>

    data class ScreenState<NavTarget : Parcelable, State : Parcelable>(
        val onScreen: NavElements<NavTarget, out State> = emptyList(),
        val offScreen: NavElements<NavTarget, out State> = emptyList(),
    )

}
