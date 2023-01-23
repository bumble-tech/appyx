package com.bumble.appyx.interactions.core.ui.property


interface Interpolatable<T> {

    suspend fun lerpTo(start: T, end: T, fraction: Float)
}
