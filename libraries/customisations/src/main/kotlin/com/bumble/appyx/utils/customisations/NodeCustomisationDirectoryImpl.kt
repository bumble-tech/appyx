package com.bumble.appyx.utils.customisations

import com.bumble.appyx.utils.customisations.NodeCustomisationDirectoryImpl.DirectoryValue.LazyDirectoryValue
import com.bumble.appyx.utils.customisations.NodeCustomisationDirectoryImpl.DirectoryValue.NonLazyDirectoryValue
import kotlin.reflect.KClass

@Suppress("TooManyFunctions")
open class NodeCustomisationDirectoryImpl(
    override val parent: NodeCustomisationDirectory? = null
) : LazyMutableNodeCustomisationDirectory {

    private val map: MutableMap<Any, DirectoryValue> = hashMapOf()

    @Deprecated(
        "Use putLazy to avoid potential performance issues",
        replaceWith = ReplaceWith("putLazy(key) { value }")
    )
    override fun <T : NodeCustomisation> put(key: KClass<T>, value: T) {
        map[key] = NonLazyDirectoryValue(value)
    }

    override fun <T : NodeCustomisation> putLazy(key: KClass<T>, valueProvider: () -> T) {
        map[key] = LazyDirectoryValue(lazy(valueProvider))
    }

    fun <T : Any> put(vararg values: T) {
        values.forEach {
            map[it::class] = NonLazyDirectoryValue(it)
        }
    }

    fun <T : Any> putLazy(vararg values: Lazy<T>) {
        values.forEach {
            map[it::class] = LazyDirectoryValue(it)
        }
    }

    inline operator fun <reified T : Any> T.unaryPlus() {
        put(this)
    }

    override fun <T : NodeCustomisation> get(key: KClass<T>): T? =
        getValue(key)

    override fun <T : NodeCustomisation> getRecursively(key: KClass<T>): T? =
        get(key) ?: parent?.get(key)

    @Deprecated(
        "Use putSubDirectoryLazy to avoid potential performance issues",
        replaceWith = ReplaceWith("putSubDirectoryLazy(key) { value }")
    )
    override fun <T : Any> putSubDirectory(key: KClass<T>, value: NodeCustomisationDirectory) {
        map[key] = NonLazyDirectoryValue(value)
    }

    override fun <T : Any> putSubDirectoryLazy(
        key: KClass<T>,
        valueProvider: () -> NodeCustomisationDirectory
    ) {
        map[key] = LazyDirectoryValue(lazy(valueProvider))
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
        map[this] = LazyDirectoryValue(lazy {
            NodeCustomisationDirectoryImpl(this@NodeCustomisationDirectoryImpl).apply(block)
        })
    }

    /**
     * Gets a value from the map, if the value is lazy the lazy value will be initialized.
     */
    private fun <T : Any> getValue(key: Any?): T? =
        map[key].let { value ->
            when (value) {
                is LazyDirectoryValue -> value.lazyValue.value
                is NonLazyDirectoryValue -> value.value
                null -> null
            }
        } as? T

    private sealed class DirectoryValue {
        data class LazyDirectoryValue(val lazyValue: Lazy<Any>) : DirectoryValue()
        data class NonLazyDirectoryValue(val value: Any) : DirectoryValue()
    }
}
