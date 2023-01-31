package com.bumble.appyx.interactions.core.ui.property

interface Animatable<T> {
    suspend fun animateTo(
        scope: CoroutineScope,
        props: T,
        onStart: () -> Unit = {},
        onFinished: () -> Unit = {}
    )
}