package com.bumble.appyx.interactions

actual object AppyxLogger {

    actual var loggingLevel: Int = 12

    actual fun v(tag: String, message: String) {
        if (loggingLevel <= AppyxLoggingLevel.VERBOSE) {
            console.log("$tag: $message")
        }
    }

    actual fun d(tag: String, message: String) {
        if (loggingLevel <= AppyxLoggingLevel.DEBUG) {
            console.log("$tag: $message")
        }
    }


    actual fun i(tag: String, message: String) {
        if (loggingLevel <= AppyxLoggingLevel.INFO) {
            console.log("$tag: $message")
        }
    }

    actual fun w(tag: String, message: String) {
        if (loggingLevel <= AppyxLoggingLevel.WARN) {
            console.log("$tag: $message")
        }
    }

    actual fun e(tag: String, message: String) {
        if (loggingLevel <= AppyxLoggingLevel.ERROR) {
            console.log("$tag: $message")
        }
    }
}
