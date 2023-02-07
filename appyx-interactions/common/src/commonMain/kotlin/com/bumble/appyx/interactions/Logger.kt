package com.bumble.appyx.interactions

expect object Logger {

    fun v(tag: String, message: String)

    fun log(tag: String, message: String)
}
