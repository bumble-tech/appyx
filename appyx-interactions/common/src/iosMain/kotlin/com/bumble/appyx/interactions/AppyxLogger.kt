package com.bumble.appyx.interactions

import com.bumble.appyx.interactions.AppyxLoggingLevel.DISABLED

actual object AppyxLogger {

    actual var loggingLevel: Int = DISABLED

    actual fun v(tag: String, message: String) {

    }

    actual fun d(tag: String, message: String) {

    }

    actual fun i(tag: String, message: String) {

    }

    actual fun w(tag: String, message: String) {

    }

    actual fun e(tag: String, message: String) {

    }

}
