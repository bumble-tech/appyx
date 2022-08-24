package com.bumble.appyx.core.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface ChildRenderer {

    @Composable
    operator fun invoke(modifier: Modifier)

    @Composable
    operator fun invoke()
}
