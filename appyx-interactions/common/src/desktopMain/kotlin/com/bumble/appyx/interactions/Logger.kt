package com.bumble.appyx.interactions

actual object Logger {

    actual fun v(tag: String, message: String) {
        println("$tag, $message")
    }
    actual fun log(tag: String, message: String) {
        println("$tag, $message")
    }
}
