package com.bumble.appyx.interactions.core.ui.property

import kotlinx.coroutines.CoroutineScope

interface Animatable<T> {

    suspend fun snapTo(
        scope: CoroutineScope,
        props: T
    )

    suspend fun animateTo(
        scope: CoroutineScope,
        props: T,
        onStart: () -> Unit = {},
        onFinished: () -> Unit = {}
    )
}
