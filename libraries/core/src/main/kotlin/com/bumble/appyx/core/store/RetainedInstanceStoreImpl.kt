package com.bumble.appyx.core.store

internal class RetainedInstanceStoreImpl : RetainedInstanceStore {

    private val map: MutableMap<String, MutableMap<String, ValueHolder<*>>> = HashMap()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> get(
        storeId: String,
        key: String,
        disposer: (T) -> Unit,
        factory: () -> T
    ): T =
        map
            .getOrPut(storeId) { HashMap() }
            .getOrPut(key) { ValueHolder(factory(), disposer) }
            .value as T

    override fun isRetainedByStoreId(storeId: String, value: Any): Boolean =
        map[storeId]?.hasValue(value) ?: false

    override fun isRetained(value: Any): Boolean =
        map.values.any { valueHolderMap -> valueHolderMap.hasValue(value) }

    override fun clearStore(storeId: String) {
        map.remove(storeId)?.values?.forEach { it.dispose() }
    }

    private fun MutableMap<String, ValueHolder<*>>.hasValue(value: Any): Boolean =
        values.any { valueHolder -> valueHolder.value === value }

    private class ValueHolder<T : Any>(
        val value: T,
        private val disposer: (T) -> Unit
    ) {
        fun dispose() {
            disposer(value)
        }
    }
}
