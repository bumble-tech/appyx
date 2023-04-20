package com.bumble.appyx.interactions

import android.util.Log
import androidx.annotation.IntRange
import com.bumble.appyx.interactions.AppyxLoggingLevel.DEBUG
import com.bumble.appyx.interactions.AppyxLoggingLevel.DISABLED
import com.bumble.appyx.interactions.AppyxLoggingLevel.ERROR
import com.bumble.appyx.interactions.AppyxLoggingLevel.INFO
import com.bumble.appyx.interactions.AppyxLoggingLevel.VERBOSE
import com.bumble.appyx.interactions.AppyxLoggingLevel.WARN

actual object AppyxLogger {

    @IntRange(from = VERBOSE.toLong(), to = DISABLED.toLong())
    actual var loggingLevel: Int = DISABLED

    actual fun v(tag: String, message: String) {
        if (loggingLevel <= VERBOSE) {
            Log.v(tag, message)
        }
    }

    actual fun d(tag: String, message: String) {
        if (loggingLevel <= DEBUG) {
            Log.d(tag, message)
        }
    }

    actual fun i(tag: String, message: String) {
        if (loggingLevel <= INFO) {
            Log.i(tag, message)
        }
    }


    actual fun w(tag: String, message: String) {
        if (loggingLevel <= WARN) {
            Log.w(tag, message)
        }
    }

    actual fun e(tag: String, message: String) {
        if (loggingLevel <= ERROR) {
            Log.e(tag, message)
        }
    }

}
