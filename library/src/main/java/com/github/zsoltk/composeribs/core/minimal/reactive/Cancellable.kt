package com.github.zsoltk.composeribs.core.minimal.reactive

interface Cancellable {
    fun cancel()

    companion object {
        fun cancellableOf(f: () -> Unit): Cancellable =
            object : Cancellable {
                override fun cancel() {
                    f()
                }
            }
    }

    object Empty : Cancellable {
        override fun cancel() { }
    }
}
