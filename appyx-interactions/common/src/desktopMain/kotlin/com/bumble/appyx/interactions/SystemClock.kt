package com.bumble.appyx.interactions

actual object SystemClock {
    actual fun nanoTime(): Long =
        System.nanoTime()
}