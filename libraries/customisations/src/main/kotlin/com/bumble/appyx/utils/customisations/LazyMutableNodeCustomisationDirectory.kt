package com.bumble.appyx.utils.customisations

import kotlin.reflect.KClass

@Deprecated("This interface will be merged into MutableNodeCustomisationDirectory as part of 1.2")
interface LazyMutableNodeCustomisationDirectory: MutableNodeCustomisationDirectory {

    fun <T : Any> putSubDirectoryLazy(key: KClass<T>, valueProvider: () -> NodeCustomisationDirectory)

    fun <T : NodeCustomisation> putLazy(key: KClass<T>, valueProvider: () -> T)
}
