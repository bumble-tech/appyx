package com.github.zsoltk.composeribs.minimal.reactive

class CompositeCancellable(vararg cancellables: Cancellable) : Cancellable {
    private val items = mutableListOf(*cancellables)
    operator fun plusAssign(item: Cancellable) {
        items += item
    }

    override fun cancel() {
        items.forEach { it.cancel() }
        items.clear()
    }
}
