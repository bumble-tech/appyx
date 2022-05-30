package com.bumble.appyx.v2.core.state

import androidx.compose.runtime.saveable.SaverScope

class MutableSavedStateMapImpl(
    private val map: MutableMap<String, Any?> = HashMap(),
    override val saverScope: SaverScope,
) : MutableSavedStateMap, MutableMap<String, Any?> by map {
    private var lock: Boolean = false

    val savedState: SavedStateMap by lazy(LazyThreadSafetyMode.NONE) {
        lock = true
        map
    }

    override fun put(key: String, value: Any?): Any? {
        checkState()
        return map.put(key, value)
    }

    override fun putAll(from: Map<out String, Any?>) {
        checkState()
        return map.putAll(from)
    }

    private fun checkState() {
        check(!lock) { "This MutableSavedStateMap has already dumped its state, it is meaningless to write anything anymore" }
    }

}
