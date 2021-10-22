package com.github.zsoltk.composeribs.core.routing.source.backstack

import java.util.concurrent.atomic.AtomicInteger

class UuidGenerator(
    initialValue: Int
) {

    private val counter = AtomicInteger(initialValue)

    fun incrementAndGet() = counter.incrementAndGet()
}
