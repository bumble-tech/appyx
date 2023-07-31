package com.bumble.appyx.interactions

import platform.CoreFoundation.CFAbsoluteTimeGetCurrent

actual object SystemClock {

    actual fun nanoTime(): Long = CFAbsoluteTimeGetCurrent().toLong()
}
