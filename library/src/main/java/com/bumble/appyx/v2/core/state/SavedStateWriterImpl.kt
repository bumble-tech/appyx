package com.bumble.appyx.v2.core.state

import androidx.compose.runtime.saveable.SaverScope

internal class SavedStateWriterImpl(
    override val saverScope: SaverScope,
) : SavedStateWriter {
    private val mergedState = HashMap<String, StateWithSource>()
    private var lock: Boolean = false

    val savedState: SavedStateMap by lazy(LazyThreadSafetyMode.NONE) {
        lock = true
        mergedState.mapValues { it.value.state }
    }

    override fun save(key: String, value: Any?, source: Any) {
        check(!lock) {
            "This writer has already dumped state, it is meaningless to write anything anymore"
        }
        val existingState = mergedState[key]
        check(existingState == null || existingState.source == source) {
            "Save instance process has faced key collision at '${key}': " +
                    "existing is written by ${existingState?.source}, " +
                    "new one by $source"
        }
        mergedState[key] = StateWithSource(value, source)
    }

    class StateWithSource(val state: Any?, val source: Any)
}
