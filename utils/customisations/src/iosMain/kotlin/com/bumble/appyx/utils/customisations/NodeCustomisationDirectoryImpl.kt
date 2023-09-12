package com.bumble.appyx.utils.customisations

import kotlin.reflect.KClass

actual open class NodeCustomisationDirectoryImpl actual constructor(
    override val parent: NodeCustomisationDirectory?
) : MutableNodeCustomisationDirectory {

    private val map: MutableMap<Any, Any> = hashMapOf()

    override fun <T : NodeCustomisation> put(key: KClass<T>, valueProvider: () -> T) {
        map[key] = lazy(valueProvider)
    }

    fun <T : Any> put(vararg values: T) {
        values.forEach {
            map[it::class] = it
        }
    }

    override fun <T : Any> putSubDirectory(
        key: KClass<T>,
        valueProvider: () -> NodeCustomisationDirectory
    ) {
        map[key] = lazy(valueProvider)
    }

    inline operator fun <reified T : Any> T.unaryPlus() {
        put(this)
    }

    override fun <T : NodeCustomisation> get(key: KClass<T>): T? =
        map[key] as? T

    override fun <T : NodeCustomisation> getRecursively(key: KClass<T>): T? =
        get(key) ?: parent?.get(key)

    override fun <T : Any> getSubDirectory(key: KClass<T>): NodeCustomisationDirectory? =
        map[key] as? NodeCustomisationDirectory

    override fun <T : Any> getSubDirectoryOrSelf(key: KClass<T>): NodeCustomisationDirectory {
        val subDir = map.keys.firstOrNull {
            it is KClass<*>
        }

        return map[subDir] as? NodeCustomisationDirectory ?: this
    }

    operator fun KClass<*>.invoke(block: NodeCustomisationDirectoryImpl.() -> Unit) {
        if (map.containsKey(this)) {
            // TODO warning for accidental override?
        }
        map[this] = NodeCustomisationDirectoryImpl(this@NodeCustomisationDirectoryImpl).apply(block)
    }
}
