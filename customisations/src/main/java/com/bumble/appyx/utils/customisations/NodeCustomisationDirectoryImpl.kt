package com.bumble.appyx.utils.customisations

import kotlin.reflect.KClass

open class NodeCustomisationDirectoryImpl(
    override val parent: NodeCustomisationDirectory? = null
) : MutableNodeCustomisationDirectory {

    private val map: MutableMap<Any, Any> = hashMapOf()

    override fun <T : NodeCustomisation> put(key: KClass<T>, value: T) {
        map[key] = value
    }

    fun <T : Any> put(vararg values: T) {
        values.forEach {
            map[it::class] = it
        }
    }

    inline operator fun <reified T : Any> T.unaryPlus() {
        put(this)
    }

    override fun <T : NodeCustomisation> get(key: KClass<T>): T? =
        map[key] as? T

    override fun <T : NodeCustomisation> getRecursively(key: KClass<T>): T? =
        get(key) ?: parent?.get(key)

    override fun <T : Any> putSubDirectory(key: KClass<T>, value: NodeCustomisationDirectory) {
        map[key] = value
    }

    override fun <T : Any> getSubDirectory(key: KClass<T>): NodeCustomisationDirectory?=
        map[key] as? NodeCustomisationDirectory

    override fun <T : Any> getSubDirectoryOrSelf(key: KClass<T>): NodeCustomisationDirectory {
        val subDir = map.keys.firstOrNull {
            it is KClass<*> && it.java.isAssignableFrom(key.java)
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
