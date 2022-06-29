package com.bumble.appyx.utils.customisations

import kotlin.reflect.KClass

interface MutableNodeCustomisationDirectory : NodeCustomisationDirectory {

    fun <T : Any> putSubDirectory(key: KClass<T>, value: NodeCustomisationDirectory)

    fun <T : NodeCustomisation> put(key: KClass<T>, value: T)
}
