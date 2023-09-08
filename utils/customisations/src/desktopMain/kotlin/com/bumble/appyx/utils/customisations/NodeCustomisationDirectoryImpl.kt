package com.bumble.appyx.utils.customisations

import kotlin.reflect.KClass

actual open class NodeCustomisationDirectoryImpl actual constructor(
    override val parent: NodeCustomisationDirectory?
) : MutableNodeCustomisationDirectory {

    private val map: MutableMap<Any, Lazy<*>> = hashMapOf()

    override fun <T : NodeCustomisation> put(key: KClass<T>, valueProvider: () -> T) {
        map[key] = lazy(valueProvider)
    }

    override fun <T : NodeCustomisation> get(key: KClass<T>): T? =
        getValue(key)

    override fun <T : NodeCustomisation> getRecursively(key: KClass<T>): T? =
        get(key) ?: parent?.get(key)

    override fun <T : Any> putSubDirectory(
        key: KClass<T>,
        valueProvider: () -> NodeCustomisationDirectory
    ) {
        map[key] = lazy(valueProvider)
    }

    override fun <T : Any> getSubDirectory(key: KClass<T>): NodeCustomisationDirectory? =
        getValue(key)

    override fun <T : Any> getSubDirectoryOrSelf(key: KClass<T>): NodeCustomisationDirectory {
        val subDir = map.keys.firstOrNull {
            it is KClass<*> && it.java.isAssignableFrom(key.java)
        }

        return getValue(subDir) ?: this
    }

    operator fun KClass<*>.invoke(block: NodeCustomisationDirectoryImpl.() -> Unit) {
        if (map.containsKey(this)) {
            // TODO warning for accidental override?
        }
        map[this] = lazy {
            NodeCustomisationDirectoryImpl(this@NodeCustomisationDirectoryImpl).apply(block)
        }
    }

    /**
     * Gets a value from the map, if the value is lazy the lazy value will be initialized.
     */
    private fun <T : Any> getValue(key: Any?): T? =
        map[key]?.value as? T

}
