package android.util

import kotlin.jvm.JvmStatic

/**
 * For unit testing, it is necessary to mock the Android Log class. Attempting
 * to mock the Logger class, as we have done before, is not feasible due to its
 * expect tag. This results in compile-time errors as the symbol is duplicated
 * from the compiler's point of view.
 */
class Log {

    companion object {
        @JvmStatic
        fun v(tag: String, msg: String): Int = 0
        @JvmStatic
        fun d(tag: String, msg: String): Int = 0

    }
}