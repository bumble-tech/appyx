package com.bumble.appyx.debug

object Appyx {

    var exceptionHandler: ((Exception) -> Unit)? = null

    fun reportException(exception: Exception) {
        val handler = exceptionHandler
        if (handler != null) {
            handler(exception)
        } else {
            throw exception
        }
    }

}