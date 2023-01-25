package com.bumble.appyx.navigation

import com.bumble.appyx.navigation.children.ChildEntry

object Appyx {

    var exceptionHandler: ((Exception) -> Unit)? = null
    var defaultChildKeepMode: ChildEntry.KeepMode = ChildEntry.KeepMode.KEEP

    fun reportException(exception: Exception) {
        val handler = exceptionHandler
        if (handler != null) {
            handler(exception)
        } else {
            throw exception
        }
    }

}
