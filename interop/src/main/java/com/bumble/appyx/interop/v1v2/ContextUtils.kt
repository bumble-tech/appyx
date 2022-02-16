package com.bumble.appyx.interop.v1v2

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

@Suppress("UNCHECKED_CAST")
internal fun <T : Activity> Context.findActivity(): T? {
    return if (this is Activity) {
        this as T?
    } else {
        val contextWrapper = this as ContextWrapper?
        val baseContext = contextWrapper?.baseContext
        requireNotNull(baseContext)
        baseContext.findActivity()
    }
}
