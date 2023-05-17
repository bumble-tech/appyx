package com.bumble.appyx.utils.multiplatform

actual class Logger {
    actual fun d(tag: String, message: String) {
        println("$tag: $message")
    }
}
