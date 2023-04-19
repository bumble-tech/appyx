package com.bumble.appyx.interactions

/**
 * For unit testing we need to mock Logger. It must have the same package name as the one used in
 * appyx-interactions module.
 */
object Logger {

    fun v(tag: String, message: String) {
    }

    fun log(tag: String, message: String) {
    }

}
