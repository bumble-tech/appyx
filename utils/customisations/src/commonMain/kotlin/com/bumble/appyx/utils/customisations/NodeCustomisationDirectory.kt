package com.bumble.appyx.utils.customisations

import kotlin.reflect.KClass

interface NodeCustomisationDirectory {

    val parent: NodeCustomisationDirectory?

    fun <T : Any> getSubDirectory(key: KClass<T>) : NodeCustomisationDirectory?

    fun <T : Any> getSubDirectoryOrSelf(key: KClass<T>) : NodeCustomisationDirectory

    fun <T : NodeCustomisation> get(key: KClass<T>) : T?

    fun <T : NodeCustomisation> getRecursively(key: KClass<T>) : T?

    fun <T : NodeCustomisation> getRecursivelyOrDefault(default: T) : T =
        get(default::class) ?: parent?.get(default::class) ?: default
}
