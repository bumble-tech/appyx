package com.bumble.appyx.core.store

/**
 * A simple storage to preserve any objects during configuration change events.
 * `factory` function will be invoked immediately on the same thread
 * only if an object of the same class within the same Node does not exist.
 *
 * The framework will manage the lifecycle of provided objects
 * and invoke `disposer` function to destroy objects properly.
 *
 * Sample usage:
 * ```kotlin
 * val feature = RetainedInstanceStore.get(
 *     storeId = buildContext.identifier,
 *     factory = { FeatureImpl() },
 *     disposer = { feature.dispose() }
 * }
 * ```
 * or
 *  * ```kotlin
 *  * val feature = buildContext.getRetainedInstance(
 *  *     factory = { FeatureImpl() },
 *  *     disposer = { feature.dispose() }
 *  * }
 *  * ```
 */
interface RetainedInstanceStore {

    fun <T : Any> get(storeId: String, key: String, disposer: (T) -> Unit = {}, factory: () -> T): T

    fun isRetainedByStoreId(storeId: String, value: Any): Boolean

    fun isRetained(value: Any): Boolean

    fun clearStore(storeId: String)

    companion object : RetainedInstanceStore by RetainedInstanceStoreImpl()

}
