package com.bumble.appyx.interactions

import kotlinx.browser.window
import kotlin.math.roundToLong

actual object SystemClock {

    actual fun nanoTime(): Long =
        (window.performance.now() * MillisToNanos).roundToLong()

    private const val MillisToNanos = 1_000_000L

}
