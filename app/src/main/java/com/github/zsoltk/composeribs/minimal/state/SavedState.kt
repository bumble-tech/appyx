package com.github.zsoltk.composeribs.minimal.state

import android.os.Bundle
import android.os.Parcelable

/**
 * Copied from MVICore
 * TODO: consider extracting MVICore version
 */
class TimeCapsule(private val savedState: Bundle?) {

    private val stateSuppliers = hashMapOf<String, () -> Parcelable>()

    fun <State : Parcelable> get(key: Any): State? = savedState?.getParcelable(key.toString())

    fun <State : Parcelable> register(key: Any, stateSupplier: () -> State) {
        stateSuppliers[key.toString()] = stateSupplier
    }

    fun saveState(outState: Bundle) {
        stateSuppliers.forEach { entry -> outState.putParcelable(entry.key, entry.value()) }
    }
}
