package com.bumble.appyx.interactions

/**
 * For unit testing we need to mock Logger. It must have the same package name as the one used in
 * appyx-interactions module.
 */
object AppyxLogger {

    fun v(tag: String, message: String) = Unit

    fun d(tag: String, message: String) = Unit

    fun i(tag: String, message: String) = Unit

    fun w(tag: String, message: String) = Unit

    fun e(tag: String, message: String) = Unit

}
