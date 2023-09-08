package com.bumble.appyx.utils.customisations

import kotlin.reflect.KClass

interface MutableNodeCustomisationDirectory : NodeCustomisationDirectory {
    fun <T : Any> putSubDirectory(key: KClass<T>, valueProvider: () -> NodeCustomisationDirectory)

    fun <T : NodeCustomisation> put(key: KClass<T>, valueProvider: () -> T)
}

inline fun <reified T : Any> MutableNodeCustomisationDirectory.putSubDirectory(
    noinline valueProvider: () -> NodeCustomisationDirectory
) {
    putSubDirectory(T::class, valueProvider)
}

inline fun <reified T : NodeCustomisation> MutableNodeCustomisationDirectory.put(
    noinline valueProvider: () -> T
) {
    put(T::class, valueProvider)
}
