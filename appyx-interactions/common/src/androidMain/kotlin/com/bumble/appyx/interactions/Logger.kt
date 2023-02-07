package com.bumble.appyx.interactions

import android.util.Log

actual object Logger {

    actual fun v(tag: String, message: String) {
        Log.v(tag, message)
    }

    actual fun log(tag: String, message: String) {
        Log.d(tag, message)
    }

}
