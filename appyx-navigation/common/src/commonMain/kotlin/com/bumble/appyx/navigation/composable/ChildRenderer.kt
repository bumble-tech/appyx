package com.bumble.appyx.navigation.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier

@Stable
interface ChildRenderer {

    @Suppress("ComposableNaming") // This wants to be 'Invoke' but that won't work with 'operator'.
    @Composable
    operator fun invoke(modifier: Modifier)

    @Suppress("ComposableNaming") // This wants to be 'Invoke' but that won't work with 'operator'.
    @Composable
    operator fun invoke()
}
