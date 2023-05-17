package com.bumble.appyx.utils.multiplatform

import android.util.Log

actual class Logger {
    actual fun d(tag: String, message: String) {
        Log.d(tag, message)
    }
}
