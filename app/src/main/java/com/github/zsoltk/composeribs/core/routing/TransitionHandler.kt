package com.github.zsoltk.composeribs.core.routing

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface TransitionHandler<S> {

    @Composable
    fun handle(
        transitionState: S,
        onRemovedFromScreen: () -> Unit,
        onDestroyed: () -> Unit,
    ): Modifier
}
