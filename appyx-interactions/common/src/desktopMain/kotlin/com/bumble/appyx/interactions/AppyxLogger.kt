package com.bumble.appyx.interactions

import com.bumble.appyx.interactions.AppyxLoggingLevel.DEBUG
import com.bumble.appyx.interactions.AppyxLoggingLevel.ERROR
import com.bumble.appyx.interactions.AppyxLoggingLevel.INFO
import com.bumble.appyx.interactions.AppyxLoggingLevel.VERBOSE
import com.bumble.appyx.interactions.AppyxLoggingLevel.WARN

actual object AppyxLogger {

    actual var loggingLevel: Int = 12

    actual fun v(tag: String, message: String) {
        if (loggingLevel <= VERBOSE) {
            println("$tag, $message")
        }
    }

    actual fun d(tag: String, message: String) {
        if (loggingLevel <= DEBUG) {
            println("$tag, $message")
        }
    }

    actual fun i(tag: String, message: String) {
        if (loggingLevel <= INFO) {
            println("$tag, $message")
        }
    }

    actual fun w(tag: String, message: String) {
        if (loggingLevel <= WARN) {
            println("$tag, $message")
        }
    }

    actual fun e(tag: String, message: String) {
        if (loggingLevel <= ERROR) {
            println("$tag, $message")
        }
    }

}
