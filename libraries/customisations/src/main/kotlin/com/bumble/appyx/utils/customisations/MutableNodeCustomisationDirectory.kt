package com.bumble.appyx.utils.customisations

import kotlin.reflect.KClass

interface MutableNodeCustomisationDirectory : NodeCustomisationDirectory {

    @Deprecated(
        message = "Use putSubDirectoryLazy to avoid potential performance issues",
        replaceWith = ReplaceWith("putSubDirectoryLazy(key) { value }")
    )
    fun <T : Any> putSubDirectory(key: KClass<T>, value: NodeCustomisationDirectory)

    @Deprecated(
        message = "Use putLazy to avoid potential performance issues",
        replaceWith = ReplaceWith("putLazy(key) { value }")
    )
    fun <T : NodeCustomisation> put(key: KClass<T>, value: T)
}
