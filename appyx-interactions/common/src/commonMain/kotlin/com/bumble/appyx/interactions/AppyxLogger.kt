package com.bumble.appyx.interactions


object AppyxLoggingLevel {
    const val VERBOSE = 1
    const val DEBUG = 2
    const val INFO = 3
    const val WARN = 4
    const val ERROR = 5
    const val DISABLED = 6
}

expect object AppyxLogger {

    var loggingLevel: Int

    fun v(tag: String, message: String)

    fun d(tag: String, message: String)

    fun i(tag: String, message: String)

    fun w(tag: String, message: String)

    fun e(tag: String, message: String)
}

