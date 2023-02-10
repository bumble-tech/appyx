package com.bumble.appyx.interactions.core.ui.property

import androidx.compose.animation.core.SpringSpec
import kotlinx.coroutines.CoroutineScope

interface Animatable<T> {

    suspend fun snapTo(
        scope: CoroutineScope,
        props: T
    )

    fun lerpTo(scope: CoroutineScope, start: T, end: T, fraction: Float)

    suspend fun animateTo(
        scope: CoroutineScope,
        props: T,
        springSpec: SpringSpec<Float>,
        onStart: () -> Unit = {},
        onFinished: () -> Unit = {}
    )
}
