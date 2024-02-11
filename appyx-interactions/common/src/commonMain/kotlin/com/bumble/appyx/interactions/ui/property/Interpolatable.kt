package com.bumble.appyx.interactions.ui.property


interface Interpolatable<T> {

    suspend fun lerpTo(start: T, end: T, fraction: Float)
}
