package com.bumble.appyx.v2.core.state

import androidx.compose.runtime.saveable.SaverScope

interface SavedStateWriter {

    val saverScope: SaverScope

    /**
     * Put [value] with [key] into [SavedStateMap].
     * This method verifies that there are no key collision, otherwise throws [IllegalStateException].
     *
     * @param source Pass `this` to be able to track source of [key]-[value] pair to properly log any collisions.
     */
    fun save(key: String, value: Any?, source: Any)

}
