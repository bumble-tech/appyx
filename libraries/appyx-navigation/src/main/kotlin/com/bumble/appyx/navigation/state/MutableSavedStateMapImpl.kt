package com.bumble.appyx.navigation.state

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
        checkKey(key, value)
        return map.put(key, value)
    }

    override fun putAll(from: Map<out String, Any?>) {
        checkState()
        from.entries.forEach { checkKey(it.key, it.value) }
        return map.putAll(from)
    }

    private fun checkState() {
        check(!lock) { "This MutableSavedStateMap has already dumped its state, it is " +
                "meaningless to write anything anymore" }
    }

    private fun checkKey(key: String, value: Any?) {
        check(!map.containsKey(key)) {
            "Save instance process has faced key collision at '${key}': " +
                    "existing value is '${map[key]}', " +
                    "new value is '$value'"
        }
    }

}
