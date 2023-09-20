package com.bumble.appyx.utils.customisations

import kotlin.reflect.KClass

actual open class NodeCustomisationDirectoryImpl actual constructor(
    override val parent: NodeCustomisationDirectory?
) : MutableNodeCustomisationDirectory {

    override fun <T : NodeCustomisation> put(key: KClass<T>, valueProvider: () -> T) {
        // NO-OP
    }

    override fun <T : NodeCustomisation> get(key: KClass<T>): T? =
        null

    override fun <T : NodeCustomisation> getRecursively(key: KClass<T>): T? =
        null

    override fun <T : Any> putSubDirectory(key: KClass<T>, valueProvider: () -> NodeCustomisationDirectory) {
        // NO-OP
    }

    override fun <T : Any> getSubDirectory(key: KClass<T>): NodeCustomisationDirectory? =
        null

    override fun <T : Any> getSubDirectoryOrSelf(key: KClass<T>): NodeCustomisationDirectory {
        return this
    }

    operator fun KClass<*>.invoke(block: NodeCustomisationDirectoryImpl.() -> Unit) {
        // NO-OP
    }
}
