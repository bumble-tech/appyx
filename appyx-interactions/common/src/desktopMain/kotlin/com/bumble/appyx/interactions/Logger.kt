package com.bumble.appyx.interactions

actual object Logger {
    actual fun log(tag: String, message: String) {
        println("$tag, $message")
    }
}
